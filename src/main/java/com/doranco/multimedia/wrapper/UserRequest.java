package com.doranco.multimedia.wrapper;

import com.doranco.multimedia.utils.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "Contact number is mandatory") @Pattern(regexp = "^\\d{10}$", message = "Invalid contact number")
    private String contactNumber;

    @NotBlank(message = "Email est obligatoire") @Email(message = "Adresse e-mail invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire") @Size(min = 8, message = "Password must be at least 8 characters long")
    @ValidPassword
    private String password;
}
