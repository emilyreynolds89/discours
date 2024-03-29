package com.codepath.fbu_newsfeed.Models;

public class Fact {

    private TruthLevel truthLevel;

    public Fact(TruthLevel truthLevel) { this.truthLevel = truthLevel; }

    public enum TruthLevel {
        TRUE, MOSTLY_TRUE, MIXTURE, MOSTLY_FALSE, FALSE, UNPROVEN, OPINION
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
            case 6:
                fact = TruthLevel.OPINION;
                break;
            case 5:
                fact = TruthLevel.TRUE;
                break;
            case 4:
                fact = TruthLevel.MOSTLY_TRUE;
                break;
            case 3:
                fact = TruthLevel.MIXTURE;
                break;
            case 2:
                fact = TruthLevel.MOSTLY_FALSE;
                break;
            case 1:
                fact = TruthLevel.FALSE;
                break;
            default:
                fact = TruthLevel.UNPROVEN;
                break;
        }
        return fact;
    }

    public static int enumToInt(TruthLevel fact) {
        int i;
        switch(fact) {
            case OPINION:
                i = 6;
                break;
            case TRUE:
                i = 5;
                break;
            case MOSTLY_TRUE:
                i = 4;
                break;
            case MIXTURE:
                i = 3;
                break;
            case MOSTLY_FALSE:
                i = 2;
                break;
            case FALSE:
                i = 1;
                break;
            default:
                i = 0;
                break;
        }
        return i;
    }

    public static TruthLevel stringToEnum(String truth) {
        TruthLevel fact;
        switch(truth) {
            case "TRUE":
                fact = TruthLevel.TRUE;
                break;
            case "MOSTLY TRUE":
                fact = TruthLevel.MOSTLY_TRUE;
                break;
            case "MIXTURE":
                fact = TruthLevel.MIXTURE;
                break;
            case "MOSTLY FALSE":
                fact = TruthLevel.MOSTLY_FALSE;
                break;
            case "FALSE":
                fact = TruthLevel.FALSE;
                break;
            case "OPINION":
                fact = TruthLevel.OPINION;
                break;
            default:
                fact = TruthLevel.UNPROVEN;
                break;
        }
        return fact;
    }

    public static String enumToString(TruthLevel fact) {
        String truth;
        switch(fact) {
            case TRUE:
                truth = "TRUE";
                break;
            case MOSTLY_TRUE:
                truth = "MOSTLY TRUE";
                break;
            case MIXTURE:
                truth = "MIXTURE";
                break;
            case MOSTLY_FALSE:
                truth = "MOSTLY FALSE";
                break;
            case FALSE:
                truth = "FALSE";
                break;
            case OPINION:
                truth = "OPINION";
                break;
            default:
                truth = "UNPROVEN";
        }
        return truth;
    }
}
