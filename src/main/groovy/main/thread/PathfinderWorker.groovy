package main.thread


import main.Main
import main.Model
import main.exception.PerIsBorkenException
import main.villager.StraightPath
import main.villager.Villager

class PathfinderWorker extends Worker {

    /*
        - [ ] kör steg nod för nod
        - [ ] för nästa steg:
          - [ ] kör 90 grader med mittersta graden pekandes mot målet
          - [ ] fördela ut graderna lika till grann noderna
            - [ ] hårdkoda det med en färdig lösning per grad för alla 360 grader.
          - [ ] lägg upp en (normal) fördelning av sannolikhet för graderna över 90 grader.
            - [ ] hårdkoda fördelningen för 0 -> 90 grader med max i 45
          - [ ] beräkna genomsnittliga sannolikheten för varje grann nod relativt till de andra noderna utifrån gradernas sannolikheter
          - [ ] omfördela sannolikheterna mot vaje nod baserat på nodens movementCost relativt till de andra noderna
     */

    static def PROBABILITIES_MODEL

    private static void calculateProbabilitiesModel() {
        if (
                degreeRange(45) != (315..359) + (0..135) ||
                degreeRange(100) != 10..190 ||
                degreeRange(300) != (210..359) + (0..30)
        ) {
            throw new PerIsBorkenException()
        }

        def test = [359, 0 ,1, 89, 90, 91, 134, 135, 136, 179, 180, 181, 224, 225, 226, 269, 270 ,271, 314, 315, 316]
        test.each { def degree ->
            def testDegrees = degreeRange(degree)
            def testRange = degreeProbabilities(testDegrees)
            def testSquares = squareProbabilities(testRange)

            if (testRange.collect { it[0] } != testDegrees) {
                throw new PerIsBorkenException()
            }

            if (Math.abs((testRange.sum { it[1] } as Double) - 100) > 0.00000001) {
                throw new PerIsBorkenException()
            }

            if (Math.abs((testSquares.probability.sum() as Double) - 100) > 0.00000001) {
                throw new PerIsBorkenException()
            }
        }

        PROBABILITIES_MODEL = (0..359).collect {
            [(it): realSquareProbabilities(squareProbabilities(degreeProbabilities(degreeRange(it))))]
        }

        println(PROBABILITIES_MODEL)
    }

    private static List<Integer> degreeRange (int degree) {
        int u = degree + 90
        int l = degree - 90
        int upper = u % 360
        int lower = l >= 0 ? l : l + 360
        (upper > lower) ? (lower..upper) : (lower..359) + (0..upper)
    }

    private static List<List<Number>> degreeProbabilities(List<Integer> degree) {
        (
                (degree[0..35]).collect { [it, 12.5/36] } +
                (degree[36..71]).collect { [it, 25/36] } +
                (degree[72..107]).collect { [it, 25/36] } +
                (degree[108..143]).collect { [it, 25/36] } +
                (degree[144..180]).collect { [it, 12.5/37] }
        )
    }

    private static List<LinkedHashMap<String, Object>> squareProbabilities(List<List<Number>> degreeProbabilities) {
        def squares = [
                [135, 180]: [0, 2], [90 , 135]: [1, 2], [45 , 90 ]: [2, 2],
                [180, 225]: [0, 1],                     [0  , 45 ]: [2, 1],
                [225, 270]: [0, 0], [270, 315]: [1, 0], [315, 360]: [2, 0],
        ]

        squares.collect { def square ->
            def squareDegrees = square.key
            def squareProbability = degreeProbabilities.sum { def degreeProbability ->
                def degree = degreeProbability[0] as int
                def probability = degreeProbability[1] as Double
                (degree >= squareDegrees[0] && degree < squareDegrees[1]) ? probability : 0
            }
            [square: square, probability: squareProbability]
        }
    }

    private static def realSquareProbabilities(List<LinkedHashMap<String, Object>> linkedHashMaps) {

        hells yeah

        linkedHashMaps
    }

    public static void main(String[] args) {
        if (true) {
            calculateProbabilitiesModel()
        }
        /*
        ClassLoader classloader = Thread.currentThread().getContextClassLoader()
        def degrees = new JsonSlurper().parse(classloader.getResourceAsStream('degreesFile.json'))

        def start = [1, 0] as int[]
        def dest = [0, -20] as int[]

        def deg = Model.toDegrees(start, dest)
        */
    }

    def update() {

        (Model.model.villagers as List<Villager>).grep { it.pathfinderWorker }.each { Villager villager ->
            def start = [villager.x, villager.y] as Double[]
            def dest = Model.generateXY()

            villager.actionQueue << new StraightPath(start, dest)
            villager.toWorkWorker()
        }
    }

    static int[] pixelToNodeIdx(int[] ints) {
        ints.collect { Model.round(it / Main.SQUARE_WIDTH) } as int[]
    }

    static List<int[]> bresenham(int[] start, int[] dest) {
        def (int x1, int y1) = start
        def (int x2, int y2) = dest
        def result = []

        // delta of exact value and rounded value of the dependent variable
        int d = 0

        int dx = Math.abs(x2 - x1)
        int dy = Math.abs(y2 - y1)

        int dx2 = 2 * dx // slope scaling factors to
        int dy2 = 2 * dy // avoid floating point

        int ix = x1 < x2 ? 1 : -1 // increment direction
        int iy = y1 < y2 ? 1 : -1

        int x = x1
        int y = y1

        if (dx >= dy) {
            while (true) {
                result << ([x, y] as int[])
                if (x == x2) {
                    break
                }
                x += ix
                d += dy2
                if (d > dx) {
                    y += iy
                    d -= dx2
                }
            }
        } else {
            while (true) {
                result << ([x, y] as int[])
                if (y == y2) {
                    break
                }
                y += iy
                d += dx2
                if (d > dy) {
                    x += ix
                    d -= dy2
                }
            }
        }

        return result
    }
}
