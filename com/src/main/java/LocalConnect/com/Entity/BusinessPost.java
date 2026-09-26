package LocalConnect.com.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class BusinessPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "Please select a category")
    private String category;
    private String locality;
    private String address;
    private String description;
    private String openingDate;
    private String contact;

    private LocalDateTime createdAt = LocalDateTime.now();
}

