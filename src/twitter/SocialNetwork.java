
package twitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

public class SocialNetwork {

    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase(java.util.Locale.ROOT);

            // Every author is a user in the network.
            followsGraph.putIfAbsent(author, new HashSet<>());

            // Find all usernames mentioned in this tweet.
            Set<String> mentions =
                    Extract.getMentionedUsers(Collections.singletonList(tweet));

            for (String mentionedUser : mentions) {
                String mentioned = mentionedUser.toLowerCase(java.util.Locale.ROOT);

                // Every mentioned user must also be represented in the network.
                followsGraph.putIfAbsent(mentioned, new HashSet<>());

                // Users cannot follow themselves.
                if (!author.equals(mentioned)) {
                    followsGraph.get(author).add(mentioned);
                }
            }
        }

        return followsGraph;
    }

    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCounts = new HashMap<>();

        // Include every user, even those with zero followers.
        for (String user : followsGraph.keySet()) {
            followerCounts.put(user, 0);
        }

        // Count how many users follow each person.
        for (Set<String> followedUsers : followsGraph.values()) {
            for (String followedUser : followedUsers) {
                followerCounts.put(
                        followedUser,
                        followerCounts.getOrDefault(followedUser, 0) + 1);
            }
        }

        List<String> influencers = new ArrayList<>(followerCounts.keySet());

        // Sort by follower count, highest first.
        Collections.sort(influencers, (user1, user2) ->
                Integer.compare(
                        followerCounts.get(user2),
                        followerCounts.get(user1)));

        return influencers;
    }
}

