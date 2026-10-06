package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Clase de ayuda para mostrar dinero siempre con 2 decimales y sin notacion
// cientifica (ejemplo: 12000000.0 se mostraria como 1.2E7 sin esta clase).
public class Formato {

    public static String dinero(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
