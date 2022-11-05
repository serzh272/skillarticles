package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import ru.skillbranch.skillarticles.data.*
import ru.skillbranch.skillarticles.data.network.res.CommentRes

interface  IArticleRepository {
    fun loadArticleContent(articleId: String): LiveData<List<MarkdownElement>?>
    fun getArticle(articleId: String): LiveData<ArticleData?>
    fun loadArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?>
    fun getAppSettings(): LiveData<AppSettings>
    fun updateSettings(appSettings: AppSettings)
    fun updateArticlePersonalInfo(info: ArticlePersonalInfo)
    fun makeCommentsDataSource(articleId: String): CommentDataSource
    suspend fun sendMessage(articleId: String, message: String, answerId: String?)
}


object ArticleRepository : IArticleRepository {
    private val local = LocalDataHolder
    private val network = NetworkDataHolder
    private val isSearchLiveData = MutableLiveData<Boolean>()
    private val prefs:PrefManager = PrefManager()

    override fun loadArticleContent(articleId: String): LiveData<List<MarkdownElement>?> {
        return network.loadArticleContent(articleId)
            .map { str ->
                str?.let { MarkdownParser.parse(it) }
            }//5s delay from network
    }
    override fun getArticle(articleId: String): LiveData<ArticleData?> {
        return local.findArticle(articleId) //2s delay from db
    }

    override fun loadArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        return local.findArticlePersonalInfo(articleId) //1s delay from db
    }

    override fun getAppSettings(): LiveData<AppSettings> = prefs.settings //from preferences
    override fun updateSettings(appSettings: AppSettings) {
        prefs.isBigText = appSettings.isBigText
        prefs.isDarkMode = appSettings.isDarkMode

    }

    override fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        local.updateArticlePersonalInfo(info)
    }

    override fun makeCommentsDataSource(articleId: String) = CommentDataSource(articleId, network)

    override suspend fun sendMessage(articleId: String, message: String, answerId: String?) {
        delay(1000)
        network.sendMessage(articleId, message, answerId)
    }

    fun getSearchStatus() = isSearchLiveData

    fun updateSearchStatus(status: Boolean) {
        isSearchLiveData.value = status
    }
}

class CommentDataSource(
    val articleId: String,
    val network: NetworkDataHolder
) : PagingSource<Int, CommentRes>() {
    override fun getRefreshKey(state: PagingState<Int, CommentRes>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        val size = state.config.pageSize
        val nextKey = anchorPage.nextKey
        val prevKey = anchorPage.prevKey
        return prevKey?.plus(size) ?: nextKey?.minus(size)
    }
    //For placeholders
    /*override fun getRefreshKey(state: PagingState<Int, CommentRes>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        return clamp(anchorPosition, 0, totalComments)
    }*/

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentRes> {
        val pageKey = params.key ?: 0
        val pageSize = params.loadSize
        return try {
            val comments = network.loadComments(articleId, pageKey, pageSize)
            val prevKey = if (pageKey > 0) pageKey.minus(pageSize) else null
            val nextKey = if (comments.isNotEmpty()) pageKey.plus(pageSize) else null
            LoadResult.Page(
                data = comments,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    override val jumpingSupported = true

}