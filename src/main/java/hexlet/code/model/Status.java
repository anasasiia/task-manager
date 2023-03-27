package hexlet.code.model;

//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.annotations.CreationTimestamp;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;
//import java.util.Date;
//
//@Entity
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "statuses")
//public class Status {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @NotBlank
//    @Size(min = 1)
//    private String name;
//
//    private String description;
//
//    @NotBlank
//    @ManyToOne
//    private TaskStatus status;
//
//    @NotBlank
//    @ManyToOne
//    private User author;
//
//    private User executor;
//
//    @CreationTimestamp
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdAt;
//}
