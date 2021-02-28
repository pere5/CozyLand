package main.utility

import main.Model

class ProbabilityUtils {

    static def calculateProbabilitiesModel() {
        (0..359).collectEntries { def degree ->
            def degreeRange = degreeRange(degree)
            def degreeProbabilities = degreeProbabilities(degreeRange)
            def (tiles, _) = tileProbabilities(degreeProbabilities)
            [(degree), tiles]
        }
    }

    static List<Integer> degreeRange(int degree) {
        int u = degree + 90
        int l = degree - 90
        int upper = u % 360
        int lower = l >= 0 ? l : l + 360
        (upper > lower) ? (lower..upper) : (lower..359) + (0..upper)
    }

    static List<List<Number>> degreeProbabilities(List<Integer> degree) {
        (degree[0..35]).collect    { [it, 20/36] } +
                (degree[36..71]).collect   { [it, 17.5/36] } +
                (degree[72..108]).collect  { [it, 25/37] } +
                (degree[109..144]).collect { [it, 17.5/36] } +
                (degree[145..180]).collect { [it, 20/36] }
    }

    static List<List<Object>> tileProbabilities(List<List<Number>> degreeProbabilities) {
        def testC = []
        def result = Model.tileDegrees.collect { def tile ->
            def sTestC = []
            def sDeg = tile.key
            def tileProbability = degreeProbabilities.sum { def degreeProbability ->
                def degree = degreeProbability[0] as int
                def prob = degreeProbability[1] as Double
                def retProb
                if (sDeg[0] < sDeg[1]) {
                    retProb = (degree >= sDeg[0] && degree <= sDeg[1]) ? prob : 0
                } else {
                    retProb = (degree >= sDeg[0] || degree <= sDeg[1]) ? prob : 0
                }
                sTestC << [d: degree, p: retProb, s: tile]
                retProb
            }
            testC << sTestC
            [tile.value, tileProbability]
        }

        return [result, testC]
    }

}
