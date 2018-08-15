package moe.lyrebird.model.twitter.refresh;

public interface RateLimited {

    int maxRequestsPer15Minutes();

    void refresh();

}
