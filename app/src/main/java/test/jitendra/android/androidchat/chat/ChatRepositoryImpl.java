package test.jitendra.android.androidchat.chat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;

import test.jitendra.android.androidchat.chat.entities.ChatMessage;
import test.jitendra.android.androidchat.chat.events.ChatEvent;
import test.jitendra.android.androidchat.domain.FirebaseHelper;
import test.jitendra.android.androidchat.lib.EventBus;
import test.jitendra.android.androidchat.lib.GreenRobotEventBus;


public class ChatRepositoryImpl implements ChatRepository {
    private String receiver;
    private FirebaseHelper helper;
    private ChildEventListener chatEventListener;

    public ChatRepositoryImpl(){
        helper = FirebaseHelper.getInstance();
    }

    @Override
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public void subscribeForChatUpates() {
        if (chatEventListener == null) {
            chatEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    String msgSender = chatMessage.getSender();
                    msgSender = msgSender.replace("_",".");

                    String currentUserEmail = helper.getAuthUserEmail();
                    chatMessage.setSentByMe(msgSender.equals(currentUserEmail));

                    ChatEvent chatEvent = new ChatEvent(chatMessage);
                    EventBus eventBus = GreenRobotEventBus.getInstance();
                    eventBus.post(chatEvent);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError firebaseError) {}
            };
            helper.getChatsReference(receiver).addChildEventListener(chatEventListener);
        }
    }

    @Override
    public void unSubscribeForChatUpates() {
        if (chatEventListener != null) {
            helper.getChatsReference(receiver).removeEventListener(chatEventListener);
        }
    }

    @Override
    public void destroyChatListener() {
        chatEventListener = null;
    }

    @Override
    public void sendMessage(String msg) {
        String keySender = helper.getAuthUserEmail().replace(".","_");
        ChatMessage chatMessage = new ChatMessage(keySender, msg);
        DatabaseReference chatsReference = helper.getChatsReference(receiver);
        chatsReference.push().setValue(chatMessage);
    }

    public void changeUserConnectionStatus(boolean online) {
        helper.changeUserConnectionStatus(online);
    }
}
