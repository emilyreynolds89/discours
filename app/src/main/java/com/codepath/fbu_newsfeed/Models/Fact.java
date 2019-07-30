package com.codepath.fbu_newsfeed.Models;

public class Fact {

    private TruthLevel truthLevel;

    public Fact(TruthLevel truthLevel) { this.truthLevel = truthLevel; }

    enum TruthLevel {
        TRUE, MOSTLY_TRUE, MIXTURE, MOSTLY_FALSE, FALSE, UNPROVEN
    }

    public TruthLevel getTruthLevel() {
        return truthLevel;
    }

    public void setTruthLevel(TruthLevel truthLevel) {
        this.truthLevel = truthLevel;
    }

    public static TruthLevel intToEnum(int i) {
        TruthLevel fact;
        switch(i) {
            case 0:
                fact = TruthLevel.TRUE;
                break;
            case 1:
                fact = TruthLevel.MOSTLY_TRUE;
                break;
            case 2:
                fact = TruthLevel.MIXTURE;
                break;
            case 3:
                fact = TruthLevel.MOSTLY_FALSE;
                break;
            case 4:
                fact = TruthLevel.FALSE;
                break;
            default:
                fact = TruthLevel.TRUE;
                break;
        }
        return fact;
    }

    public int enumToInt(TruthLevel fact) {
        int i;
        switch(fact) {
            case TRUE:
                i = 0;
                break;
            case MOSTLY_TRUE:
                i = 1;
                break;
            case MIXTURE:
                i = 2;
                break;
            case MOSTLY_FALSE:
                i = 3;
                break;
            case FALSE:
                i = 4;
                break;
            default:
                i = 0;
        }
        return i;
    }
}
