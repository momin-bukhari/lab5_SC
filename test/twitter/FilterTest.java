
package twitter;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/*
 * Testing strategy for writtenBy(tweets, username):
 *   number of tweets: 0, 1, > 1
 *   number of tweets by username: 0, 1, > 1
 *   case of username: same as author, different from author
 *
 * Testing strategy for inTimespan(tweets, timespan):
 *   number of tweets: 0, > 1
 *   tweets inside timespan: none, some, all
 *   tweet exactly on start or end of timespan: yes, no
 *
 * Testing strategy for containing(tweets, words):
 *   number of words: 1, > 1
 *   matching tweets: none, some, all
 *   case of word: same as in tweet, different
 *   word appears only as part of a bigger word: yes, no
 *
 * Every result is also checked for keeping the input order.
 */
public class FilterTest {

    private static final Instant d1 =
            Instant.parse("2016-02-17T10:00:00Z");

    private static final Instant d2 =
            Instant.parse("2016-02-17T11:00:00Z");

    private static final Instant d3 =
            Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 =
            new Tweet(1, "alyssa",
                    "is it reasonable to talk about rivest so much?", d1);

    private static final Tweet tweet2 =
            new Tweet(2, "bbitdiddle",
                    "rivest talk in 30 minutes #hype", d2);

    private static final Tweet tweet3 =
            new Tweet(3, "Alyssa",
                    "Talking about the pset tonight", d3);

    private static final List<Tweet> tweets =
            Arrays.asList(tweet1, tweet2, tweet3);

    // writtenBy tests

    @Test
    public void testWrittenByNoTweets() {
        assertEquals(Collections.emptyList(),
                Filter.writtenBy(Collections.emptyList(), "alyssa"));
    }

    @Test
    public void testWrittenByOneResult() {
        assertEquals(Arrays.asList(tweet1),
                Filter.writtenBy(Arrays.asList(tweet1), "alyssa"));
    }

    @Test
    public void testWrittenByDifferentCaseMultipleResults() {
        List<Tweet> writtenBy = Filter.writtenBy(tweets, "ALYSSA");

        assertEquals("expected two tweets in order",
                Arrays.asList(tweet1, tweet3), writtenBy);
    }

    @Test
    public void testWrittenByNoMatchingAuthor() {
        assertEquals(Collections.emptyList(),
                Filter.writtenBy(tweets, "nobody"));
    }

    // inTimespan tests

    @Test
    public void testInTimespanNoTweets() {
        assertEquals(Collections.emptyList(),
                Filter.inTimespan(Collections.emptyList(),
                        new Timespan(d1, d3)));
    }

    @Test
    public void testInTimespanEndpointsIncluded() {
        List<Tweet> inTimespan =
                Filter.inTimespan(tweets, new Timespan(d2, d3));

        assertEquals("expected tweets on the endpoints",
                Arrays.asList(tweet2, tweet3), inTimespan);
    }

    @Test
    public void testInTimespanAllTweets() {
        assertEquals(tweets,
                Filter.inTimespan(tweets, new Timespan(d1, d3)));
    }

    @Test
    public void testInTimespanNoMatchingTweets() {
        Instant start = Instant.parse("2016-02-18T10:00:00Z");
        Instant end = Instant.parse("2016-02-18T11:00:00Z");

        assertEquals(Collections.emptyList(),
                Filter.inTimespan(tweets, new Timespan(start, end)));
    }

    @Test
    public void testInTimespanPreservesInputOrder() {
        List<Tweet> unordered =
                Arrays.asList(tweet3, tweet1, tweet2);

        assertEquals(Arrays.asList(tweet3, tweet1, tweet2),
                Filter.inTimespan(unordered, new Timespan(d1, d3)));
    }

    // containing tests

    @Test
    public void testContainingCaseAndWholeWords() {
        List<Tweet> containing =
                Filter.containing(tweets, Arrays.asList("TALK"));

        assertEquals("expected tweets 1 and 2 only",
                Arrays.asList(tweet1, tweet2), containing);
    }

    @Test
    public void testContainingMultipleWords() {
        List<Tweet> containing =
                Filter.containing(tweets, Arrays.asList("talk", "rivest"));

        assertEquals(Arrays.asList(tweet1, tweet2), containing);
    }

    @Test
    public void testContainingNoMatchingWords() {
        assertEquals(Collections.emptyList(),
                Filter.containing(tweets, Arrays.asList("elephant")));
    }

    @Test
    public void testContainingEmptyWordList() {
        assertEquals(Collections.emptyList(),
                Filter.containing(tweets, Collections.emptyList()));
    }

    @Test
    public void testContainingDoesNotMatchPartOfWord() {
        assertEquals(Collections.emptyList(),
                Filter.containing(Arrays.asList(tweet3),
                        Arrays.asList("talk")));
    }

    @Test
    public void testContainingPreservesInputOrder() {
        List<Tweet> unordered =
                Arrays.asList(tweet2, tweet1, tweet3);

        assertEquals(Arrays.asList(tweet2, tweet1),
                Filter.containing(unordered, Arrays.asList("talk")));
    }
}

