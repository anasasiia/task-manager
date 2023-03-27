package hexlet.code.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private final int minSize = 3;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 1)
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    @Size(min = minSize)
    private String password;
}
