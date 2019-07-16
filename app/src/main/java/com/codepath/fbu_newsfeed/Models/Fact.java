package com.codepath.fbu_newsfeed.Models;

public class Fact {

    public TruthLevel truthLevel;

    public Fact(TruthLevel truthLevel) { this.truthLevel = truthLevel; }

    enum TruthLevel {
        TRUE, M0STLY_TRUE, MIXTURE, MOSTLY_FALSE, FALSE, UNPROVEN;
    }

    public TruthLevel getTruthLevel() {
        return truthLevel;
    }

    public void setTruthLevel(TruthLevel truthLevel) {
        this.truthLevel = truthLevel;
    }
}
