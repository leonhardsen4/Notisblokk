package com.leonhardsen.notisblokk.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notes {

    private int id;
    private int id_tag;
    private String data;
    private String titulo;
    private String relato;
    private String status;

}
