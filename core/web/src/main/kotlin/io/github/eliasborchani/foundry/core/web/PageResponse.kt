package io.github.eliasborchani.foundry.core.web

/**
 * Generic paginated response wrapper. Used by controllers that return sliced results.
 *
 * Example:
 *   @GetMapping fun list(...): PageResponse<UserDto> = PageResponse.of(items, total, page, size)
 */
data class PageResponse<T>(
    val items: List<T>,
    val total: Long,
    val page: Int,
    val size: Int,
    val totalPages: Int,
) {
    companion object {
        fun <T> of(items: List<T>, total: Long, page: Int, size: Int): PageResponse<T> =
            PageResponse(
                items = items,
                total = total,
                page = page,
                size = size,
                totalPages = if (size == 0) 0 else ((total + size - 1) / size).toInt(),
            )
    }
}
