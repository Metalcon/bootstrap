package de.metalcon.bootstrap.domain.entities;

import de.metalcon.domain.Muid;

public class Recommendation {

    private Muid from;

    private Muid to;

    private Integer score;

    public Recommendation(
            Muid from,
            Muid to,
            Integer score) {
        this.from = from;
        this.to = to;
        this.score = score;
    }

    public Muid getFrom() {
        return from;
    }

    public void setFrom(Muid from) {
        this.from = from;
    }

    public Muid getTo() {
        return to;
    }

    public void setTo(Muid to) {
        this.to = to;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
