
package edu.undra.styckers.util;

/**
 * This class models rating texts.<br>
 * Some types may need rating and a text that properly describes a rating. 
 * 
 * @date 21 de jul. de 2022 12.59.15
 * @author alexandre
 */
public class RatingTexts {

//    /RATING TEXTS
    private final String fiveStars;//THE SUPREME 
    private final String fourStars;//THE BEST
    private final String threeStars;//THE NOT BAD
    private final String twoStars;//THE BAD
    private final String oneStar;//THE LOWEST

    public RatingTexts(String fiveStars, String fourStars, String threeStars, String twoStars, String oneStar) {
        this.fiveStars = fiveStars;
        this.fourStars = fourStars;
        this.threeStars = threeStars;
        this.twoStars = twoStars;
        this.oneStar = oneStar;
    }

    public String fiveStars() {
        return fiveStars;
    }

    public String fourStars() {
        return fourStars;
    }

    public String threeStars() {
        return threeStars;
    }

    public String twoStars() {
        return twoStars;
    }

    public String oneStar() {
        return oneStar;
    }

    
    
    
    
    
    
}
