package com.codepath.fbu_newsfeed.Models;

public class Bias {

    private BiasType bias;

    public Bias(BiasType bias) {
        this.bias = bias;
    }


    public BiasType getBias() {
        return bias;
    }

    public void setBias(BiasType bias) {
        this.bias = bias;
    }


    public enum BiasType {
        LIBERAL, SLIGHTLY_LIBERAL, MODERATE, SLIGHTLY_CONSERVATIVE, CONSERVATIVE
    }


    public static BiasType intToEnum(int i) {
        BiasType res;
        switch(i) {
            case 1:
                res = BiasType.LIBERAL;
                break;
            case 2:
                res = BiasType.SLIGHTLY_LIBERAL;
                break;
            case 3:
                res = BiasType.MODERATE;
                break;
            case 4:
                res = BiasType.SLIGHTLY_CONSERVATIVE;
                break;
            case 5:
                res = BiasType.CONSERVATIVE;
                break;
            default:
                res = BiasType.MODERATE;
        }
        return res;
    }

    public static int enumToInt(BiasType bias) {
        int res;
        switch(bias) {
            case LIBERAL:
                res = 1;
                break;
            case SLIGHTLY_LIBERAL:
                res = 2;
                break;
            case MODERATE:
                res = 3;
                break;
            case SLIGHTLY_CONSERVATIVE:
                res = 4;
                break;
            case CONSERVATIVE:
                res = 5;
                break;
            default:
                res = 3;
        }
        return res;
    }
}
