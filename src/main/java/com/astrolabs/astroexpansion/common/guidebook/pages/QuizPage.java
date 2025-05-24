package com.astrolabs.astroexpansion.common.guidebook.pages;

import com.astrolabs.astroexpansion.api.guidebook.IGuidePage;
import com.astrolabs.astroexpansion.common.guidebook.AstroGuideBook;
import com.astrolabs.astroexpansion.common.guidebook.GuideProgress;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Interactive quiz page with scoring and rewards
 */
public class QuizPage implements IGuidePage {
    private final ResourceLocation quizId;
    private final Component title;
    private final List<QuizQuestion> questions;
    private final int pageNumber;
    
    private int currentQuestion = 0;
    private int score = 0;
    private boolean quizComplete = false;
    private final Map<Integer, Integer> selectedAnswers = new HashMap<>();
    
    public QuizPage(ResourceLocation quizId, Component title, int pageNumber) {
        this.quizId = quizId;
        this.title = title;
        this.pageNumber = pageNumber;
        this.questions = new ArrayList<>();
    }
    
    public void addQuestion(String question, String[] answers, int correctAnswer) {
        questions.add(new QuizQuestion(question, answers, correctAnswer));
    }
    
    public void addQuestion(String question, String[] answers, int correctAnswer, String explanation) {
        questions.add(new QuizQuestion(question, answers, correctAnswer, explanation));
    }
    
    @Override
    public String getType() {
        return "quiz";
    }
    
    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        // Title
        int titleWidth = Minecraft.getInstance().font.width(title);
        graphics.drawString(Minecraft.getInstance().font, title, 
            x + width / 2 - titleWidth / 2, y + 5, 0x000000, false);
        
        if (quizComplete) {
            renderResults(graphics, x, y, width, height);
        } else if (currentQuestion < questions.size()) {
            renderQuestion(graphics, x, y, width, height, mouseX, mouseY);
        }
        
        // Progress bar
        if (!quizComplete) {
            int barY = y + height - 20;
            int barWidth = width - 20;
            graphics.fill(x + 10, barY, x + 10 + barWidth, barY + 10, 0xFF333333);
            
            int progress = (currentQuestion * barWidth) / questions.size();
            graphics.fill(x + 10, barY, x + 10 + progress, barY + 10, 0xFF00AA00);
            
            String progressText = String.format("Question %d / %d", currentQuestion + 1, questions.size());
            int progressWidth = Minecraft.getInstance().font.width(progressText);
            graphics.drawString(Minecraft.getInstance().font, progressText,
                x + width / 2 - progressWidth / 2, barY + 1, 0xFFFFFF, false);
        }
    }
    
    private void renderQuestion(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        QuizQuestion question = questions.get(currentQuestion);
        
        // Question text
        List<String> lines = wrapText(question.question, width - 20);
        int textY = y + 25;
        for (String line : lines) {
            graphics.drawString(Minecraft.getInstance().font, line, x + 10, textY, 0x000000, false);
            textY += 10;
        }
        
        // Answers
        int answerY = textY + 15;
        Integer selected = selectedAnswers.get(currentQuestion);
        
        for (int i = 0; i < question.answers.length; i++) {
            boolean isSelected = selected != null && selected == i;
            boolean isHovered = mouseX >= x + 20 && mouseX < x + width - 20 &&
                              mouseY >= answerY && mouseY < answerY + 20;
            
            // Answer box
            int bgColor = isSelected ? 0xFF0088FF : (isHovered ? 0xFFCCCCCC : 0xFFEEEEEE);
            graphics.fill(x + 20, answerY, x + width - 20, answerY + 20, bgColor);
            graphics.fill(x + 21, answerY + 1, x + width - 21, answerY + 19, 0xFFFFFFFF);
            
            // Radio button
            int radioX = x + 25;
            int radioY = answerY + 10;
            graphics.fill(radioX - 4, radioY - 4, radioX + 4, radioY + 4, 0xFF666666);
            graphics.fill(radioX - 3, radioY - 3, radioX + 3, radioY + 3, 0xFFFFFFFF);
            if (isSelected) {
                graphics.fill(radioX - 2, radioY - 2, radioX + 2, radioY + 2, 0xFF0088FF);
            }
            
            // Answer text
            MutableComponent answerText = Component.literal(String.format("%c) %s", 'A' + i, question.answers[i]));
            graphics.drawString(Minecraft.getInstance().font, answerText, x + 40, answerY + 6, 0x000000, false);
            
            answerY += 25;
        }
        
        // Submit button
        if (selected != null) {
            int buttonY = answerY + 10;
            int buttonWidth = 80;
            int buttonX = x + width / 2 - buttonWidth / 2;
            boolean buttonHovered = mouseX >= buttonX && mouseX < buttonX + buttonWidth &&
                                  mouseY >= buttonY && mouseY < buttonY + 20;
            
            graphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + 20, 
                buttonHovered ? 0xFF00CC00 : 0xFF00AA00);
            
            Component buttonText = Component.literal("Submit");
            int textWidth = Minecraft.getInstance().font.width(buttonText);
            graphics.drawString(Minecraft.getInstance().font, buttonText,
                buttonX + buttonWidth / 2 - textWidth / 2, buttonY + 6, 0xFFFFFF, false);
        }
    }
    
    private void renderResults(GuiGraphics graphics, int x, int y, int width, int height) {
        // Results header
        MutableComponent header = Component.literal("Quiz Complete!").withStyle(ChatFormatting.BOLD);
        int headerWidth = Minecraft.getInstance().font.width(header);
        graphics.drawString(Minecraft.getInstance().font, header,
            x + width / 2 - headerWidth / 2, y + 30, 0x00AA00, false);
        
        // Score
        float percentage = (score * 100f) / questions.size();
        MutableComponent scoreText = Component.literal(String.format("Score: %d / %d (%.1f%%)", 
            score, questions.size(), percentage));
        
        ChatFormatting color = percentage >= 80 ? ChatFormatting.GREEN : 
                              percentage >= 60 ? ChatFormatting.YELLOW : ChatFormatting.RED;
        scoreText.withStyle(color);
        
        int scoreWidth = Minecraft.getInstance().font.width(scoreText);
        graphics.drawString(Minecraft.getInstance().font, scoreText,
            x + width / 2 - scoreWidth / 2, y + 50, color.getColor(), false);
        
        // Grade
        String grade = percentage >= 90 ? "A+" : percentage >= 80 ? "A" :
                      percentage >= 70 ? "B" : percentage >= 60 ? "C" : "F";
        MutableComponent gradeText = Component.literal("Grade: " + grade)
            .withStyle(ChatFormatting.BOLD)
            .withStyle(color);
        int gradeWidth = Minecraft.getInstance().font.width(gradeText);
        graphics.drawString(Minecraft.getInstance().font, gradeText,
            x + width / 2 - gradeWidth / 2, y + 70, color.getColor(), false);
        
        // Review section
        graphics.drawString(Minecraft.getInstance().font, "Review:", x + 10, y + 100, 0x000000, false);
        
        int reviewY = y + 115;
        for (int i = 0; i < questions.size() && reviewY < y + height - 30; i++) {
            QuizQuestion q = questions.get(i);
            Integer selected = selectedAnswers.get(i);
            boolean correct = selected != null && selected == q.correctAnswer;
            
            // Question number with icon
            String icon = correct ? "✓" : "✗";
            ChatFormatting iconColor = correct ? ChatFormatting.GREEN : ChatFormatting.RED;
            
            MutableComponent review = Component.literal(String.format("Q%d %s", i + 1, icon))
                .withStyle(iconColor);
            graphics.drawString(Minecraft.getInstance().font, review, x + 15, reviewY, iconColor.getColor(), false);
            
            // Show correct answer if wrong
            if (!correct && q.explanation != null) {
                MutableComponent hint = Component.literal(" → " + q.explanation)
                    .withStyle(ChatFormatting.GRAY)
                    .withStyle(ChatFormatting.ITALIC);
                graphics.drawString(Minecraft.getInstance().font, hint, x + 50, reviewY, 0x666666, false);
            }
            
            reviewY += 12;
        }
        
        // Retry button
        int buttonY = y + height - 40;
        int buttonWidth = 80;
        int buttonX = x + width / 2 - buttonWidth / 2;
        graphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + 20, 0xFF0088FF);
        
        Component buttonText = Component.literal("Retry");
        int textWidth = Minecraft.getInstance().font.width(buttonText);
        graphics.drawString(Minecraft.getInstance().font, buttonText,
            buttonX + buttonWidth / 2 - textWidth / 2, buttonY + 6, 0xFFFFFF, false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        
        if (quizComplete) {
            // Retry button
            int buttonY = 140;
            if (mouseY >= buttonY && mouseY < buttonY + 20) {
                resetQuiz();
                return true;
            }
        } else if (currentQuestion < questions.size()) {
            // Answer selection
            int answerY = 80;
            QuizQuestion question = questions.get(currentQuestion);
            
            for (int i = 0; i < question.answers.length; i++) {
                if (mouseY >= answerY && mouseY < answerY + 20) {
                    selectedAnswers.put(currentQuestion, i);
                    Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.5f, 1.0f);
                    return true;
                }
                answerY += 25;
            }
            
            // Submit button
            if (selectedAnswers.containsKey(currentQuestion)) {
                if (mouseY >= answerY + 10 && mouseY < answerY + 30) {
                    submitAnswer();
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void submitAnswer() {
        Integer selected = selectedAnswers.get(currentQuestion);
        if (selected != null) {
            QuizQuestion question = questions.get(currentQuestion);
            if (selected == question.correctAnswer) {
                score++;
                Minecraft.getInstance().player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
            } else {
                Minecraft.getInstance().player.playSound(SoundEvents.VILLAGER_NO, 0.5f, 1.0f);
            }
            
            currentQuestion++;
            
            if (currentQuestion >= questions.size()) {
                completeQuiz();
            }
        }
    }
    
    private void completeQuiz() {
        quizComplete = true;
        Player player = Minecraft.getInstance().player;
        
        // Save score
        ((GuideProgress)AstroGuideBook.getInstance().getProgress(player)).getQuizScores().put(quizId, score);
        
        // Achievement sound
        if (score == questions.size()) {
            player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        } else {
            player.playSound(SoundEvents.PLAYER_LEVELUP, 0.5f, 1.0f);
        }
    }
    
    private void resetQuiz() {
        currentQuestion = 0;
        score = 0;
        quizComplete = false;
        selectedAnswers.clear();
    }
    
    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (Minecraft.getInstance().font.width(currentLine + " " + word) > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines;
    }
    
    @Override
    public void tick() {}
    
    @Override
    public Component getTooltip(int mouseX, int mouseY) {
        return null;
    }
    
    @Override
    public boolean isInteractive() {
        return true;
    }
    
    @Override
    public int getPageNumber() {
        return pageNumber;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }
    
    @Override
    public void onOpened(Player player) {
        // Load saved progress if any
        int savedScore = AstroGuideBook.getInstance().getProgress(player).getQuizScore(quizId);
        if (savedScore > 0) {
            // Show previous score
        }
    }
    
    @Override
    public void onClosed(Player player) {}
    
    private static class QuizQuestion {
        final String question;
        final String[] answers;
        final int correctAnswer;
        final String explanation;
        
        QuizQuestion(String question, String[] answers, int correctAnswer) {
            this(question, answers, correctAnswer, null);
        }
        
        QuizQuestion(String question, String[] answers, int correctAnswer, String explanation) {
            this.question = question;
            this.answers = answers;
            this.correctAnswer = correctAnswer;
            this.explanation = explanation;
        }
    }
}