package com.stellae.app.domain.model

enum class DignityType(val points: Int, val displayName: String) {
    DOMICILE(5, "Domicile"),
    EXALTATION(4, "Exaltation"),
    TRIPLICITY(3, "Triplicity"),
    TERM(2, "Term"),
    DECAN(1, "Face/Decan"),
    DETRIMENT(-5, "Detriment"),
    FALL(-4, "Fall"),
    PEREGRINE(0, "Peregrine")
}
