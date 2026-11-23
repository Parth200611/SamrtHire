
package com.example.smarthire.Recruiter.Ai;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecruiterAIFragment extends Fragment {

    private static final String API_URL = Urls.chat_aii_rect;

    private ImageView btnBack;
    private EditText etMessage;
    private MaterialButton btnSend;

    private LinearLayout layoutWelcome;
    private LinearLayout layoutMessages;
    private ScrollView scrollChat;

    private TextView suggestionResume;
    private TextView suggestionInterview;
    private TextView suggestionJobs;

    private RequestQueue requestQueue;

    // Stores previous successful conversation messages.
    private final ArrayList<JSONObject> chatHistory = new ArrayList<>();

    private boolean isRequestRunning = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_recruiter_a_i,
                container,
                false
        );

        // Initialize Volley RequestQueue.
        requestQueue = Volley.newRequestQueue(requireContext());

        // Initialize views.
        btnBack = view.findViewById(R.id.btn_back);
        etMessage = view.findViewById(R.id.et_message);
        btnSend = view.findViewById(R.id.btn_send);

        layoutWelcome = view.findViewById(R.id.layout_welcome);
        layoutMessages = view.findViewById(R.id.layout_messages);
        scrollChat = view.findViewById(R.id.scroll_chat);

        suggestionResume = view.findViewById(R.id.suggestion_resume);
        suggestionInterview = view.findViewById(R.id.suggestion_interview);
        suggestionJobs = view.findViewById(R.id.suggestion_jobs);

        // Initial screen.
        layoutWelcome.setVisibility(View.VISIBLE);
        layoutMessages.setVisibility(View.GONE);

        // Back button.
        btnBack.setOnClickListener(v -> {

            hideKeyboard();

            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed();
            }
        });

        // Send button.
        btnSend.setOnClickListener(v -> sendMessage());

        // Recruiter AI suggestions.
        if (suggestionResume != null) {
            suggestionResume.setOnClickListener(v ->
                    sendSuggestion(
                            "How can I find the best candidates for my job?"
                    )
            );
        }

        if (suggestionInterview != null) {
            suggestionInterview.setOnClickListener(v ->
                    sendSuggestion(
                            "Generate technical interview questions for a Java developer."
                    )
            );
        }

        if (suggestionJobs != null) {
            suggestionJobs.setOnClickListener(v ->
                    sendSuggestion(
                            "Help me write an attractive and professional job description."
                    )
            );
        }

        // Send message using keyboard.
        etMessage.setOnEditorActionListener((textView, actionId, event) -> {

            boolean sendAction =
                    actionId == EditorInfo.IME_ACTION_SEND;

            boolean enterAction =
                    event != null
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            && event.getAction() == KeyEvent.ACTION_DOWN;

            if (sendAction || enterAction) {
                sendMessage();
                return true;
            }

            return false;
        });

        return view;
    }

    // ----------------------------------------------------
    // SEND SUGGESTION
    // ----------------------------------------------------

    private void sendSuggestion(String message) {

        if (isRequestRunning) {
            return;
        }

        etMessage.setText(message);
        sendMessage();
    }

    // ----------------------------------------------------
    // SEND MESSAGE
    // ----------------------------------------------------

    private void sendMessage() {

        if (isRequestRunning || etMessage == null) {
            return;
        }

        String userMessage = etMessage.getText()
                .toString()
                .trim();

        if (userMessage.isEmpty()) {
            etMessage.setError("Please enter your message");
            return;
        }

        if (userMessage.length() > 5000) {
            etMessage.setError("Message cannot exceed 5000 characters");
            return;
        }

        hideKeyboard();

        etMessage.setText("");

        // Hide welcome screen after first message.
        layoutWelcome.setVisibility(View.GONE);
        layoutMessages.setVisibility(View.VISIBLE);

        // Display user message.
        addChatBubble(userMessage, true);

        // Display typing indicator.
        TextView typingView = addTypingIndicator();

        isRequestRunning = true;

        btnSend.setEnabled(false);
        btnSend.setText("...");

        // Call PHP AI API.
        callChatApi(userMessage, typingView);
    }

    // ----------------------------------------------------
    // CALL PHP CHAT API
    // ----------------------------------------------------

    private void callChatApi(String userMessage, TextView typingView) {

        JSONObject requestBody = new JSONObject();

        try {

            requestBody.put("message", userMessage);

            JSONArray historyArray = new JSONArray();

            // Send previous successful conversation messages.
            for (JSONObject historyItem : chatHistory) {
                historyArray.put(historyItem);
            }

            requestBody.put("history", historyArray);

        } catch (JSONException e) {

            removeTypingIndicator(typingView);
            finishRequest();

            addChatBubble(
                    "Unable to prepare your message. Please try again.",
                    false
            );

            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                API_URL,
                requestBody,

                response -> {

                    // Avoid updating a destroyed Fragment view.
                    if (!isAdded() || layoutMessages == null) {
                        return;
                    }

                    removeTypingIndicator(typingView);
                    finishRequest();

                    try {

                        boolean status = response.optBoolean(
                                "status",
                                false
                        );

                        if (!status) {

                            String message = response.optString(
                                    "message",
                                    "Unable to generate a response."
                            );

                            addChatBubble(message, false);
                            return;
                        }

                        String aiAnswer = response.optString(
                                "answer",
                                ""
                        ).trim();

                        if (aiAnswer.isEmpty()) {

                            addChatBubble(
                                    "Sorry, I could not generate a response. Please try again.",
                                    false
                            );

                            return;
                        }

                        // Display AI response.
                        addChatBubble(aiAnswer, false);

                        // Save successful conversation.
                        addHistoryMessage("user", userMessage);
                        addHistoryMessage("assistant", aiAnswer);

                    } catch (Exception e) {

                        addChatBubble(
                                "Something went wrong while reading the AI response.",
                                false
                        );
                    }
                },

                error -> {

                    if (!isAdded() || layoutMessages == null) {
                        return;
                    }

                    removeTypingIndicator(typingView);
                    finishRequest();

                    String errorMessage = getVolleyErrorMessage(error);

                    addChatBubble(errorMessage, false);
                }
        );

        // Groq API may take time to respond.
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        65000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        request.setTag("SmartHireChatRequest");

        if (requestQueue != null) {
            requestQueue.add(request);
        }
    }

    // ----------------------------------------------------
    // SAVE CONVERSATION HISTORY
    // ----------------------------------------------------

    private void addHistoryMessage(String role, String content) {

        try {

            JSONObject item = new JSONObject();

            item.put("role", role);
            item.put("content", content);

            chatHistory.add(item);

            // Keep latest 10 messages.
            while (chatHistory.size() > 10) {
                chatHistory.remove(0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    // DISPLAY CHAT BUBBLE
    // ----------------------------------------------------

    private void addChatBubble(String message, boolean isUser) {

        if (getContext() == null || layoutMessages == null) {
            return;
        }

        LinearLayout row = new LinearLayout(requireContext());

        row.setOrientation(LinearLayout.HORIZONTAL);

        row.setGravity(
                isUser ? Gravity.END : Gravity.START
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rowParams.setMargins(
                dp(8),
                dp(5),
                dp(8),
                dp(5)
        );

        TextView messageView = new TextView(requireContext());

        messageView.setText(message);
        messageView.setTextSize(15);

        messageView.setTextColor(
                isUser ? Color.WHITE : Color.rgb(15, 23, 42)
        );

        messageView.setPadding(
                dp(15),
                dp(11),
                dp(15),
                dp(11)
        );

        messageView.setMaxWidth(
                (int) (getResources()
                        .getDisplayMetrics()
                        .widthPixels * 0.78)
        );

        messageView.setLineSpacing(dp(2), 1.0f);

        GradientDrawable background = new GradientDrawable();

        background.setCornerRadius(dp(18));

        if (isUser) {
            background.setColor(Color.rgb(29, 78, 216));
        } else {
            background.setColor(Color.WHITE);
        }

        messageView.setBackground(background);

        row.addView(messageView);

        layoutMessages.addView(row, rowParams);

        scrollToBottom();
    }

    // ----------------------------------------------------
    // TYPING INDICATOR
    // ----------------------------------------------------

    private TextView addTypingIndicator() {

        TextView typingView = new TextView(requireContext());

        typingView.setText("SmartHire AI is typing...");
        typingView.setTextSize(13);
        typingView.setTextColor(Color.DKGRAY);

        typingView.setPadding(
                dp(14),
                dp(8),
                dp(14),
                dp(8)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                dp(12),
                dp(5),
                dp(12),
                dp(5)
        );

        layoutMessages.addView(typingView, params);

        scrollToBottom();

        return typingView;
    }

    private void removeTypingIndicator(TextView typingView) {

        if (typingView != null
                && typingView.getParent() == layoutMessages) {

            layoutMessages.removeView(typingView);
        }
    }

    // ----------------------------------------------------
    // REQUEST STATE
    // ----------------------------------------------------

    private void finishRequest() {

        isRequestRunning = false;

        if (btnSend != null) {
            btnSend.setEnabled(true);
            btnSend.setText("Send");
        }
    }

    // ----------------------------------------------------
    // API ERROR MESSAGE
    // ----------------------------------------------------

    private String getVolleyErrorMessage(VolleyError error) {

        if (error.networkResponse != null
                && error.networkResponse.data != null) {

            try {

                String responseText = new String(
                        error.networkResponse.data,
                        java.nio.charset.StandardCharsets.UTF_8
                );

                JSONObject errorObject = new JSONObject(responseText);

                String serverMessage = errorObject.optString(
                        "message",
                        ""
                );

                if (!serverMessage.isEmpty()) {
                    return serverMessage;
                }

            } catch (Exception ignored) {
            }
        }

        if (error instanceof com.android.volley.TimeoutError) {
            return "The AI is taking too long to respond. Please try again.";
        }

        if (error instanceof com.android.volley.NoConnectionError) {
            return "Unable to connect to the server. Check your internet connection.";
        }

        return "Unable to connect to SmartHire AI. Please try again.";
    }

    // ----------------------------------------------------
    // SCROLL TO BOTTOM
    // ----------------------------------------------------

    private void scrollToBottom() {

        if (scrollChat != null) {

            scrollChat.post(() ->
                    scrollChat.fullScroll(View.FOCUS_DOWN)
            );
        }
    }

    // ----------------------------------------------------
    // HIDE KEYBOARD
    // ----------------------------------------------------

    private void hideKeyboard() {

        if (etMessage == null || getContext() == null) {
            return;
        }

        InputMethodManager manager =
                (InputMethodManager) requireContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (manager != null) {

            manager.hideSoftInputFromWindow(
                    etMessage.getWindowToken(),
                    0
            );
        }

        etMessage.clearFocus();
    }

    // ----------------------------------------------------
    // DP CONVERSION
    // ----------------------------------------------------

    private int dp(float value) {

        if (getContext() == null) {
            return (int) value;
        }

        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    // ----------------------------------------------------
    // FRAGMENT VIEW DESTROY
    // ----------------------------------------------------

    @Override
    public void onDestroyView() {

        // Cancel pending requests belonging to this Fragment.
        if (requestQueue != null) {
            requestQueue.cancelAll("SmartHireChatRequest");
            requestQueue = null;
        }

        isRequestRunning = false;

        // Release references to destroyed views.
        btnBack = null;
        etMessage = null;
        btnSend = null;

        layoutWelcome = null;
        layoutMessages = null;
        scrollChat = null;

        suggestionResume = null;
        suggestionInterview = null;
        suggestionJobs = null;

        super.onDestroyView();
    }
}
