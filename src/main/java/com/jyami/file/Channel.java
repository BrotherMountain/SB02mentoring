package com.jyami.file;

import java.io.Serial;
import java.io.Serializable;

public record Channel(String name) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
