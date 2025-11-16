package tw.waterballsa.dto;

/**
 * Standard pagination metadata DTO.
 *
 * Used in list responses to provide pagination information:
 * - Order history (10 items per page)
 * - Owned courses (20 items per page)
 * - Achievements (20 items per page)
 *
 * Format:
 * {
 *   "current_page": 1,
 *   "page_size": 10,
 *   "total_items": 45,
 *   "total_pages": 5
 * }
 */
public class PaginationResponse {

    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public PaginationResponse() {
    }

    public PaginationResponse(int currentPage, int pageSize, long totalItems) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
    }

    // Getters and Setters
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * Check if there is a next page.
     */
    public boolean hasNext() {
        return currentPage < totalPages;
    }

    /**
     * Check if there is a previous page.
     */
    public boolean hasPrevious() {
        return currentPage > 1;
    }
}
