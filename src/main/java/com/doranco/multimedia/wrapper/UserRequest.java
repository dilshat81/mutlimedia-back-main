package com.doranco.multimedia.wrapper;

import com.doranco.multimedia.utils.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
