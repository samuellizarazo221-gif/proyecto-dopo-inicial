import java.util.ArrayList;

/**
 * Resuelve y simula el problema de la maratón (ICPC World Finals 2025,
 * Problem I: Slot Machine).
 *
 * La máquina tiene n ruedas (3 &lt;= n &lt;= 50) con los mismos n símbolos en el
 * mismo orden. No se pueden ver los símbolos: solo se puede girar una rueda
 * una cantidad de pasos y preguntar cuántos símbolos distintos se ven. Hay
 * que dejar todas las ruedas mostrando el mismo símbolo con a lo sumo
 * 10 000 acciones.
 *
 * SlotMachine se usa únicamente como herramienta de prueba (constructor
 * SlotMachine(n), spin(wheel, steps) y distinctSymbols()) y, en simulate,
 * como simulador (makeVisible()). Nunca se leen los colores.
 *
 * Idea de la solución. Sea p[i] el símbolo que muestra la rueda i. Si se
 * gira la rueda i paso a paso durante una vuelta completa, la cantidad de
 * símbolos distintos solo toma dos valores y es la menor exactamente en
 * los pasos t en que la rueda i queda igual a otra rueda. Así se obtiene
 * W[i] = { p[j] - p[i] }, el conjunto de símbolos presentes visto desde la
 * rueda i. Como W[i] = W[1] + (p[1] - p[i]), basta encontrar el
 * desplazamiento entre ambos conjuntos para saber cuánto girar la rueda i.
 * Si W[1] es periódico ese desplazamiento no es único; en ese caso se
 * mueve la rueda 1 una vez para romper la simetría. Una vuelta de
 * exploración cuesta n acciones, así que en total se usan unas n*n + 3n.
 */
public final class SlotMachineContest
{
    private static final int MIN_N = 3;
    private static final int MAX_N = 50;
    private static final int MAX_ACTIONS = 10000;

    private final SlotMachine machine;
    private final int n;
    private final ArrayList<int[]> log;

    /**
     * Señal interna para terminar en cuanto todas las ruedas coinciden.
     */
    private static final class Solved extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
    }

    private SlotMachineContest(SlotMachine machine, int n)
    {
        this.machine = machine;
        this.n = n;
        this.log = new ArrayList<int[]>();
    }

    /**
     * Resuelve el problema para una máquina nueva de n ruedas y n
     * símbolos. La máquina permanece invisible. Solo es posible resolver
     * 3 &lt;= n &lt;= 50.
     *
     * @param n cantidad de ruedas y de símbolos
     * @return las acciones necesarias para ganar, como pares
     *         {rueda, pasos} (rueda desde 1; los pasos pueden ser
     *         negativos), a lo sumo una por rueda; arreglo vacío si n no
     *         está en el rango del problema
     */
    public static int[][] solve(int n)
    {
        if (n < MIN_N || n > MAX_N)
        {
            return new int[0][];
        }

        return solve(new SlotMachine(n), n);
    }

    /**
     * Resuelve una máquina ya creada (que debe tener n ruedas con los
     * mismos n símbolos en el mismo orden) y la deja ganadora. Solo usa
     * de la máquina el constructor SlotMachine(n), spin(wheel, steps) y
     * distinctSymbols(); por eso el tamaño n se recibe como parámetro.
     * Si la máquina ya es ganadora o n no está en el rango del problema,
     * no hace nada y devuelve un arreglo vacío.
     *
     * @param machine máquina a resolver
     * @param n cantidad de ruedas y de símbolos de la máquina
     * @return las acciones netas que llevan la máquina a ganar
     */
    static int[][] solve(SlotMachine machine, int n)
    {
        if (n < MIN_N || n > MAX_N || machine.distinctSymbols() == 1)
        {
            return new int[0][];
        }

        return new SlotMachineContest(machine, n).solution();
    }

    /**
     * Resuelve una máquina nueva de n ruedas y n símbolos con la máquina
     * invisible y luego la muestra en su configuración inicial, aplicando
     * visiblemente (paso a paso) las acciones que la llevan a ganar. No
     * hace nada si n no está en el rango del problema (3 &lt;= n &lt;= 50).
     *
     * @param n cantidad de ruedas y de símbolos
     */
    public static void simulate(int n)
    {
        if (n < MIN_N || n > MAX_N)
        {
            return;
        }

        SlotMachine machine = new SlotMachine(n);
        int[][] actions = solve(machine, n);

        for (int[] action : actions)
        {
            machine.spin(action[0], -action[1]);
        }

        machine.makeVisible();

        for (int[] action : actions)
        {
            machine.spin(action[0], action[1]);
        }
    }

    /**
     * Resuelve la máquina y devuelve las acciones netas (una por rueda
     * con giro distinto de cero, ordenadas por rueda).
     */
    private int[][] solution()
    {
        try
        {
            findAndApplyShifts();
        }
        catch (Solved done)
        {
            // todas las ruedas ya coinciden
        }

        return netActions();
    }

    /**
     * Ejecuta el algoritmo completo sobre la máquina.
     */
    private void findAndApplyShifts()
    {
        boolean[] same = scan(1);
        boolean[] reference = view(same);

        while (isPeriodic(reference))
        {
            breakSymmetry(same);
            same = scan(1);
            reference = view(same);
        }

        int[] shift = new int[n + 1];

        for (int wheel = 2; wheel <= n; wheel++)
        {
            shift[wheel] = findShift(reference, view(scan(wheel)));
        }

        for (int wheel = 2; wheel <= n; wheel++)
        {
            if (shift[wheel] != 0)
            {
                spin(wheel, shift[wheel]);
            }
        }
    }

    /**
     * Gira una rueda un paso a la vez durante una vuelta completa y
     * registra cuántos símbolos distintos se ven en cada paso. La rueda
     * queda como estaba. Cuesta n acciones.
     *
     * @return arreglo en que la posición t es true si, al girar la rueda
     *         t pasos, queda igual a alguna otra rueda
     */
    private boolean[] scan(int wheel)
    {
        int[] distinct = new int[n];
        int lowest;
        boolean[] same = new boolean[n];

        distinct[0] = machine.distinctSymbols();
        for (int t = 1; t < n; t++)
        {
            distinct[t] = spin(wheel, 1);
        }
        spin(wheel, 1);

        lowest = distinct[0];
        for (int t = 1; t < n; t++)
        {
            lowest = Math.min(lowest, distinct[t]);
        }
        for (int t = 0; t < n; t++)
        {
            same[t] = (distinct[t] == lowest);
        }

        return same;
    }

    /**
     * Conjunto de símbolos presentes visto desde la rueda: el resultado
     * de scan, más la posición 0 (el símbolo de la propia rueda).
     */
    private boolean[] view(boolean[] same)
    {
        boolean[] result = same.clone();
        result[0] = true;
        return result;
    }

    /**
     * Indica si el conjunto se repite al desplazarlo alguna cantidad
     * distinta de cero (en ese caso el desplazamiento no es único).
     */
    private boolean isPeriodic(boolean[] set)
    {
        for (int shift = 1; shift < n; shift++)
        {
            if (matches(set, set, shift))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Indica si other[(t + shift) mod n] == base[t] para todo t.
     */
    private boolean matches(boolean[] base, boolean[] other, int shift)
    {
        for (int t = 0; t < n; t++)
        {
            if (other[(t + shift) % n] != base[t])
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Desplazamiento único que lleva el conjunto de referencia al de la
     * rueda; es lo que hay que girar esa rueda para igualar a la rueda 1.
     */
    private int findShift(boolean[] reference, boolean[] other)
    {
        for (int shift = 0; shift < n; shift++)
        {
            if (matches(reference, other, shift))
            {
                return shift;
            }
        }

        throw new IllegalStateException("No hay desplazamiento consistente.");
    }

    /**
     * Mueve la rueda 1 una vez para que el conjunto de símbolos deje de
     * ser periódico. Si la rueda 1 es la única con su símbolo, se junta
     * con el símbolo de otra rueda; si no, se lleva a un símbolo que
     * ninguna rueda muestra.
     */
    private void breakSymmetry(boolean[] same)
    {
        boolean shared = same[0];
        int target = 1;

        while (same[target] == shared)
        {
            target++;
        }

        spin(1, target);
    }

    /**
     * Gira una rueda, registra la acción y devuelve la cantidad de
     * símbolos distintos que se ven después. Termina en cuanto todas las
     * ruedas coinciden.
     */
    private int spin(int wheel, int steps)
    {
        if (log.size() >= MAX_ACTIONS)
        {
            throw new IllegalStateException("Se superaron las 10000 acciones.");
        }

        machine.spin(wheel, steps);
        log.add(new int[] {wheel, steps});

        int distinct = machine.distinctSymbols();
        if (distinct == 1)
        {
            throw new Solved();
        }

        return distinct;
    }

    /**
     * Suma los giros registrados por rueda y deja, para cada rueda con
     * giro neto distinto de cero, una sola acción con el camino más
     * corto (entre -n/2 y n/2).
     */
    private int[][] netActions()
    {
        int[] net = new int[n + 1];
        ArrayList<int[]> result = new ArrayList<int[]>();

        for (int[] action : log)
        {
            net[action[0]] = Math.floorMod(net[action[0]] + action[1], n);
        }

        for (int wheel = 1; wheel <= n; wheel++)
        {
            int steps = (net[wheel] > n / 2) ? net[wheel] - n : net[wheel];
            if (steps != 0)
            {
                result.add(new int[] {wheel, steps});
            }
        }

        return result.toArray(new int[0][]);
    }
}
