package com.leonhardsen.notisblokk.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tags {

    private int id;
    private String tag;

    public String toString() {
        return tag;
    }

}
