package com.cyberwallet.walletapi.enums;

public enum Role {
    USER,
    ADMIN
}

/*Este enum se usará:

Para determinar las autoridades de cada usuario en Spring Security.

Para controlar el acceso a distintos endpoints según el rol (más adelante con anotaciones como
@PreAuthorize("hasRole('ADMIN')")).

¿Se pueden agregar más roles en el futuro?
Sí, simplemente agregás nuevas constantes al enum, por ejemplo MANAGER, AUDITOR, etc.*/