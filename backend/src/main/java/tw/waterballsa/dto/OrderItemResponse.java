package tw.waterballsa.dto;

import tw.waterballsa.model.OrderItem;

import java.math.BigDecimal;

/**
 * DTO for order item information.
 *
 * @author Water Ball SA
 */
public class OrderItemResponse {

    private Long orderItemId;
    private Long courseId;
    private String courseTitle;
    private String courseCoverImageUrl;
    private BigDecimal price;

    // Constructors

    public OrderItemResponse() {
    }

    public OrderItemResponse(OrderItem orderItem) {
        this.orderItemId = orderItem.getOrderItemId();
        this.courseId = orderItem.getCourse().getCourseId();
        this.courseTitle = orderItem.getCourse().getTitle();
        this.courseCoverImageUrl = orderItem.getCourse().getCoverImageUrl();
        this.price = orderItem.getPrice();
    }

    // Getters and Setters

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseCoverImageUrl() {
        return courseCoverImageUrl;
    }

    public void setCourseCoverImageUrl(String courseCoverImageUrl) {
        this.courseCoverImageUrl = courseCoverImageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
