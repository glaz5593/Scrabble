package com.moshe.glaz.scrabble.managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.enteties.sudoku.Game;
import com.moshe.glaz.scrabble.enteties.RegistrationUser;
import com.moshe.glaz.scrabble.enteties.SuggestionGame;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.infrastructure.TextUtils;

import java.util.ArrayList;
import java.util.Date;

public class FirebaseManager {
    private static final String TAG = "FireBaseManager";
    private static FirebaseManager instance;
    public static FirebaseManager getInstance() {
        if(instance == null){
            instance = new FirebaseManager();
        }
        return instance;
    }

    FirebaseDatabase database;

    DatabaseReference usersReference;
    DatabaseReference suggestionGamesReference;
    DatabaseReference gameReference;
    DatabaseReference registrationReference;

    private FirebaseManager(){
        database = FirebaseDatabase.getInstance();
        gameReference = database.getReference("game");
        usersReference= database.getReference("users");
        suggestionGamesReference= database.getReference("suggestionGames");
        registrationReference= database.getReference("registrationUsers");
    }

    // region suggestionGame
    //
    //

    public void getSuggestionGames(ActionListener listener) {
        suggestionGamesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<SuggestionGame> list=new ArrayList<>();

                for(DataSnapshot s : snapshot.getChildren()){
                    SuggestionGame u=snapshot.getValue(SuggestionGame.class);
                    if(u!=null && !u.canceled) {
                        list.add(u);
                    }
                }

                listener.onResult(ActionResult.toSuccess(list));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onResult(getErrorResult(error));
            }
        });
    }




    public void registerToSuggestionGameUpdate(ActionListener listener) {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                onUpdate(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                onUpdate(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                onUpdate(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }

            private void onUpdate(DataSnapshot dataSnapshot){
                SuggestionGame suggestionGame = dataSnapshot.getValue(SuggestionGame.class);
                if(listener!= null){
                    listener.onResult(ActionResult.toSuccess(suggestionGame));
                }
            }
        };
        suggestionGamesReference.addChildEventListener(childEventListener);
    }
    public void loadSuggestionGames(ActionListener listener){
        suggestionGamesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SuggestionGame> suggestionGames =new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    SuggestionGame u=postSnapshot.getValue(SuggestionGame.class);
                    if(u!=null && !u.canceled) {
                        suggestionGames.add(u);
                    }
                }

                SuggestionGame[] arr=new SuggestionGame[suggestionGames.size()];
                arr=suggestionGames .toArray(arr);
                listener.onResult(ActionResult.toSuccess(arr));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onResult(getErrorResult(error));
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    public void updateSuggestionGame(SuggestionGame suggestionGame,ActionListener listener) {
        suggestionGamesReference.child(suggestionGame.uid).setValue(suggestionGame).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                listener.onResult(ActionResult.toSuccess(suggestionGame));
            }else{
                 listener.onResult(getErrorResult(task));
            }
        });
    }

    public void removeSuggestionGame(SuggestionGame suggestionGame, ActionListener listener) {
        suggestionGamesReference.child(suggestionGame.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addSuggestionGame(SuggestionGame suggestionGame,ActionListener listener) {
        DatabaseReference ref= suggestionGamesReference.push();
        suggestionGame.uid = ref.getKey();
        ref.setValue(suggestionGame).addOnCompleteListener(task->{
            if(task.isSuccessful()){
                listener.onResult(ActionResult.toSuccess(suggestionGame));
            }else{
                listener.onResult(getErrorResult(task));
            }
        });
    }
    // endregion

    // region users
    //
    //
    public void registerToUserUpdate(ActionListener listener) {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                onUpdate(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                onUpdate(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
             }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                onUpdate(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }

            private void onUpdate(DataSnapshot dataSnapshot){
                User user = dataSnapshot.getValue(User.class);
                if(listener!= null){
                    listener.onResult(ActionResult.toSuccess(user));
                }
            }
        };
        usersReference.addChildEventListener(childEventListener);
    }
    public void loadUsers(ActionListener listener){
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> users =new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User u=postSnapshot.getValue(User.class);
                    if(u!=null) {
                        users.add(u);
                    }
                }

                listener.onResult(ActionResult.toSuccess(users));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onResult(getErrorResult(error));
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    public void getUserByKey(String key,ActionListener listener){
        usersReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                listener.onResult(ActionResult.toSuccess(user));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onResult(getErrorResult(error));
            }
        });
    }
    public void getRegistrationUserByUserName(String userName,ActionListener listener) {
        registrationReference.orderByChild("userName").equalTo(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<RegistrationUser> users = new ArrayList<>();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        RegistrationUser user = issue.getValue(RegistrationUser.class);
                        users.add(user);
                    }

                    if (users.size() > 0) {
                        listener.onResult(ActionResult.toSuccess(users));
                        return;
                    }
                }

                listener.onResult(ActionResult.toError(TextUtils.getStringResorce(R.string.no_user_found)));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onResult(getErrorResult(error));
            }
        });
    }

    public void registration(RegistrationUser registrationUser,User user,ActionListener listener) {
        DatabaseReference registrationRef = registrationReference.push();
        DatabaseReference usersRef = usersReference.push();

        registrationUser.uid=registrationRef.getKey();
        user.uid=usersRef.getKey();
        user.registrationDate=new Date();

        registrationUser.userUid = user.uid;


        usersRef.setValue(user).addOnCompleteListener(userTask->{
            if(userTask.isSuccessful()){
                registrationRef.setValue(registrationUser).addOnCompleteListener(registerTask->{
                    if(registerTask.isSuccessful()){
                        listener.onResult(ActionResult.toSuccess(registrationUser.userUid));
                    }else{
                         listener.onResult(getErrorResult(registerTask));
                    }
                });
            }else{
                listener.onResult(getErrorResult(userTask));
            }
        });
    }


    public void updateUser(User user,ActionListener listener) {
        usersReference.child(user.uid).setValue(user).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                listener.onResult(ActionResult.toSuccess(user));
            }else{
                listener.onResult(getErrorResult(task));
            }
        });
    }

    // endregion

    // region game
    //
    //
    public void addGame(Game game,ActionListener listener) {
        DatabaseReference ref= gameReference.push();
        game.uid = ref.getKey();
        ref.setValue(game).addOnCompleteListener(task->{
            if(task.isSuccessful()){
                listener.onResult(ActionResult.toSuccess(game));
            }else{
                listener.onResult(getErrorResult(task));
            }
        });
    }

    public void updateGame(Game game,ActionListener listener){
        gameReference.child(game.uid).setValue(game).addOnCompleteListener(task->{
            if(task.isSuccessful()){
                listener.onResult(ActionResult.toSuccess(game));
            }else{
                listener.onResult(getErrorResult(task));
            }
        });
    }

    public void getGame(String uid,ActionListener listener){
        gameReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game game = dataSnapshot.getValue(Game.class);
                listener.onResult(ActionResult.toSuccess(game));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onResult(getErrorResult(error));
            }
        });
    }
    // endregion

    private ActionResult getErrorResult(DatabaseError error) {
        return ActionResult.toError(error.toException().toString());
    }

    private ActionResult getErrorResult(Task<Void> task) {
      return ActionResult.toError(getErrorMessage(task));
    }

    private String getErrorMessage(Task<Void> task) {
        return TextUtils.getStringResorce(R.string.fire_base_failed) + "\n\n" + task.getException().getMessage();
    }
}

