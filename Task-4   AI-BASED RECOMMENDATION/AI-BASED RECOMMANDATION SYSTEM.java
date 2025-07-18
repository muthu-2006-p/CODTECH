import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class Main {

    static Map<String, Map<String, Integer>> userRatings = new HashMap<>();

    public static void main(String[] args) {
        // Load sample data
        loadSampleData();

        // Create Frame
        JFrame frame = new JFrame("Recommendation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        // Heading
        JLabel heading = new JLabel("🎯 Simple Java Recommendation System", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 16));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel userLabel = new JLabel("Select User:");
        JComboBox<String> userDropdown = new JComboBox<>(userRatings.keySet().toArray(new String[0]));
        JButton recommendButton = new JButton("Get Recommendations");

        // Output Area
        JTextArea outputArea = new JTextArea(12, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Add Action to Button
        recommendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = (String) userDropdown.getSelectedItem();
                List<String> recommendations = getRecommendationsForUser(selectedUser);

                StringBuilder output = new StringBuilder();
                output.append("===================================\n");
                output.append("🎯 RECOMMENDATION SYSTEM - OUTPUT\n");
                output.append("===================================\n");
                output.append("📌 Target User: ").append(selectedUser).append("\n");
                output.append("📝 User Ratings: ").append(userRatings.get(selectedUser)).append("\n\n");

                if (recommendations.isEmpty()) {
                    output.append("❗ No new recommendations found.\n");
                } else {
                    output.append("✅ Recommended Items:\n");
                    for (String item : recommendations) {
                        output.append("➡️ ").append(item).append("\n");
                    }
                }

                output.append("===================================\n");
                outputArea.setText(output.toString());
            }
        });

        // Add Components to Panel
        inputPanel.add(userLabel);
        inputPanel.add(userDropdown);
        inputPanel.add(recommendButton);

        // Add Components to Frame
        frame.add(heading, BorderLayout.NORTH);
        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Load sample data
    private static void loadSampleData() {
        userRatings.put("User1", Map.of("Laptop", 5, "Phone", 3));
        userRatings.put("User2", Map.of("Laptop", 4, "Tablet", 5, "Phone", 2));
        userRatings.put("User3", Map.of("Tablet", 5, "Phone", 4));
    }

    // Recommendation logic
    private static List<String> getRecommendationsForUser(String user) {
        Set<String> alreadyRated = userRatings.get(user).keySet();
        Map<String, Integer> itemScores = new HashMap<>();

        for (String otherUser : userRatings.keySet()) {
            if (otherUser.equals(user)) continue;

            Map<String, Integer> otherRatings = userRatings.get(otherUser);
            for (Map.Entry<String, Integer> entry : otherRatings.entrySet()) {
                String item = entry.getKey();
                int rating = entry.getValue();

                if (!alreadyRated.contains(item) && rating >= 4) {
                    itemScores.put(item, itemScores.getOrDefault(item, 0) + rating);
                }
            }
        }

        List<String> sortedItems = new ArrayList<>(itemScores.keySet());
        sortedItems.sort((a, b) -> itemScores.get(b) - itemScores.get(a));
        return sortedItems;
    }
}
