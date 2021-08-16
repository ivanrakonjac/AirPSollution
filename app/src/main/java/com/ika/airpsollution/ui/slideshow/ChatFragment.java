package com.ika.airpsollution.ui.slideshow;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ika.airpsollution.MainActivity;
import com.ika.airpsollution.R;
import com.ika.airpsollution.db.FirebaseDB;
import com.ika.airpsollution.messages.Message;
import com.ika.airpsollution.messages.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;


    private ProgressBar progressBar;
    private ImageButton photoPickerButton;
    private EditText messageEditText;
    private Button sendButton;

    private static ChildEventListener childEventListener;

    private ListView messageListView;
    private static MessageAdapter messageAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize references to views
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        messageListView = (ListView) root.findViewById(R.id.messageListView);
        photoPickerButton = (ImageButton) root.findViewById(R.id.photoPickerButton);
        messageEditText = (EditText) root.findViewById(R.id.messageEditText);
        sendButton = (Button) root.findViewById(R.id.sendButton);

        // Initialize message ListView and its adapter
        List<Message> messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this.getContext(), R.layout.item_message, messageList);
        messageListView.setAdapter(messageAdapter);

        // Initialize progress bar
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        photoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
            }
        });

        // Enable Send button when there's text to send
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Message newMessage = new Message(messageEditText.getText().toString(), MainActivity.userName, null);
                FirebaseDB.getMessagesDbReference().push().setValue(newMessage);

                // Clear input box
                messageEditText.setText("");
            }
        });

        attachDatabaseReadListener();

//        final TextView textView = root.findViewById(R.id.text_slideshow);
//        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        messageAdapter.clear();
        detachDatabaseReadListener();
    }

    public static void attachDatabaseReadListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Message message = snapshot.getValue(Message.class);
                    messageAdapter.add(message);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            FirebaseDB.getMessagesDbReference().addChildEventListener(childEventListener);
        }
    }

    public static void detachDatabaseReadListener() {
        if (childEventListener != null) {
            FirebaseDB.getMessagesDbReference().removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    public static void clearMessageAdapter(){
        if(messageAdapter != null){
            messageAdapter.clear();
        }
    }
}