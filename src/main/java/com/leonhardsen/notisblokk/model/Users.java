package com.leonhardsen.notisblokk.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Users {

    private int id;
    private String user;
    private String password;

}
