package com.np.ratelimitedservice.util;

import java.util.Random;

public class RandomGenerator {

    private static final Random RANDOM = new Random();
    private static final String[] WORDS = new String[]{
            "Lorem",
            "ipsum",
            "dolor",
            "sit",
            "amet",
            "consectetuer",
            "adipiscing",
            "elit",
            "Maecenas",
            "porttitor",
            "congue",
            "massa",
            "Fusce",
            "posuere",
            "magna",
            "sed",
            "pulvinar",
            "ultricies",
            "purus",
            "lectus",
            "malesuada",
            "libero"
    };

    private static final String[] NAMES = new String[]{
            "John",
            "Katy",
            "Leon",
            "Merry",
            "Nick",
            "Olivia",
            "Peter",
            "Quin",
            "Rod",
            "Sue",
    };

    private static final String[] SURNAMES = new String[]{
            "Anderson",
            "Brady",
            "Collins",
            "Dawson",
            "Eckhart",
            "Ferguson",
            "Godfrey",
            "Hart",
            "Ingram",
    };


    public static String getRandomText(int minWordsCount, int maxWordsCount) {
        StringBuilder strBuilder = new StringBuilder();
        // [1, maxWordsCount]
        int textLength = RANDOM.nextInt(maxWordsCount - minWordsCount + 1) + minWordsCount;
        for (int i = 0; i < textLength; i++) {
            int rndIndex = RANDOM.nextInt(WORDS.length);
            strBuilder.append(WORDS[rndIndex]).append(" ");
        }

        return strBuilder.toString().trim();
    }

    public static String getRandomFullName() {
        int firstNameIndex = RANDOM.nextInt(NAMES.length);
        int lastNameIndex = RANDOM.nextInt(SURNAMES.length);

        return String.format("%s %s", NAMES[firstNameIndex], SURNAMES[lastNameIndex]);
    }
}
