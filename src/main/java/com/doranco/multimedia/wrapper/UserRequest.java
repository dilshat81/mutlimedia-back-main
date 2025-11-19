package com.doranco.multimedia.wrapper;

import com.doranco.multimedia.utils.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotBlank(message = "Le nom est Obligatoire")
    String name;

    @NotBlank(message = "Numero de contact obligatoire") @Pattern(regexp = "^\\d{10}$", message = "Numero de contact pas valide")
    String contactNumber;

    @NotBlank(message = "Email est obligatoire") @Email(message = "Adresse e-mail invalide")
    String email;

    @NotBlank(message = "Le mot de passe est obligatoire") @Size(min = 8, message = "Password must be at least 8 characters long")
    @ValidPassword
    String password;
}
