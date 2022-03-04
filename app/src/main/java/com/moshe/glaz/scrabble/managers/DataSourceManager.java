package com.moshe.glaz.scrabble.managers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.app.AppBase;
import com.moshe.glaz.scrabble.enteties.scrabble.Game;
import com.moshe.glaz.scrabble.enteties.SuggestionGame;
import com.moshe.glaz.scrabble.enteties.SuggestionGames;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.enteties.Users;
import com.moshe.glaz.scrabble.enteties.sudoku.DataSource;
import com.moshe.glaz.scrabble.infrastructure.UIUtils;
import com.moshe.glaz.scrabble.infrastructure.Utils;

public class DataSourceManager {
    public static int EMPTY_AVATAR_RES_ID = R.drawable.ic_round_pest_control_rodent_24;

    private static DataSourceManager instance;

    public static DataSourceManager getInstance() {
        if (instance == null) {
            instance = new DataSourceManager();
        }

        return instance;
    }

    public interface UpdateListener {
        void onGameUpdate(Game game);

        void onOnUserUpdate(User user);

        void onOnSuggestionGameUpdate(SuggestionGame suggestionGame);

        void onOnSuggestionGamesChange();

        void onOnUsersChange();

        void onError(String error);
    }

    private UpdateListener updateListener;

    enum e_loadingStatus {none, success, error}

    public void setUpdateListener(UpdateListener listener) {
        this.updateListener = listener;
    }

    DataSourceManager() {
        initAvatars();
        initUsers();
        initSuggestionGames();
        readSudokuSamples();
    }

    public void refreshData() {
        loadUsersThread();
        loadSuggestionGamesThread();
    }

    // region Avatars
    //
    //
    private ArrayList<Integer> avatarResIdList;

    private void initAvatars() {
        if (avatarResIdList == null) {
            avatarResIdList = new ArrayList<>();
            int iconResId = EMPTY_AVATAR_RES_ID;
            while (iconResId >= 0) {
                avatarResIdList.add(iconResId);
                iconResId = UIUtils.getResId("avatars_freebie_svg_" + String.format("%02d", (avatarResIdList.size())), R.drawable.class);
            }
        }
    }

    public int getAvatarResId(int avatarId) {
        initAvatars();
        return avatarResIdList.get(avatarId);
    }

    public ArrayList<Integer> getAvatarResList() {
        initAvatars();
        return avatarResIdList;
    }

    // endregion
    //
    // region Users
    //
    private Users users;
    private Thread userLoadingThread;
    private e_loadingStatus loadingUsersStatus = e_loadingStatus.none;

    private void initUsers() {
        users = PreferencesManager.getInstance().getUsers();
        loadUsersThread();
    }

    public void registerToUserUpdate() {
        FirebaseManager.getInstance().registerToUserUpdate(onUserUpdate());
    }

    private ActionListener onUserUpdate() {
        return result -> {
            if (result.success) {
                User user = (User) result.result;
                if (user == null) {
                    return;
                }

                if (!users.containsKey(user.uid)) {
                    users.put(user.uid, user);
                    if (updateListener != null) {
                        updateListener.onOnUsersChange();
                    }
                } else {
                    users.get(user.uid).copy(user);
                    if (updateListener != null) {
                        updateListener.onOnUserUpdate(user);
                    }
                }

                PreferencesManager.getInstance().saveUsers(users);
            }
        };
    }

    private void loadUsersThread() {
        userLoadingThread = new Thread(() -> {
            while (loadingUsersStatus != e_loadingStatus.success) {
                FirebaseManager.getInstance().loadUsers(result -> {
                    if (result.success) {
                        if (loadingUsersStatus == e_loadingStatus.success) {
                            return;
                        }

                        loadingUsersStatus = e_loadingStatus.success;
                        users = new Users((ArrayList<User>) result.result);

                        PreferencesManager.getInstance().saveUsers(users);
                        if (updateListener != null) {
                            UIUtils.runOnUI(() -> {
                                updateListener.onOnUsersChange();
                            });
                        }
                        registerToUserUpdate();
                        userLoadingThread = null;
                    } else {
                        if (loadingUsersStatus == e_loadingStatus.success) {
                            return;
                        }

                        loadingUsersStatus = e_loadingStatus.error;
                        if (updateListener != null) {
                            UIUtils.runOnUI(() -> {
                                updateListener.onError(result.error);
                            });
                        }

                    }
                });

                Utils.sleep(30000);
            }
        });
        userLoadingThread.setPriority(Thread.MIN_PRIORITY);
        userLoadingThread.start();
    }

    public ArrayList<User> getUsersAsList() {
        return (ArrayList<User>) users.asList();
    }

    public User getUser(String userUid) {
        if (!users.containsKey(userUid)) {
            return null;
        }

        return users.get(userUid);
    }
    // endregion

    // region SuggestionGames
    //
    private SuggestionGames suggestionGames;
    private Thread suggestionGamesLoadingThread;
    private e_loadingStatus loadingSuggestionGamesStatus = e_loadingStatus.none;

    private void initSuggestionGames() {
        suggestionGames = PreferencesManager.getInstance().getSuggestionGames();
        loadSuggestionGamesThread();
    }

    public void registerToSuggestionGameUpdate() {
        FirebaseManager.getInstance().registerToSuggestionGameUpdate(onSuggestionGameUpdate());
    }

    private ActionListener onSuggestionGameUpdate() {
        return result -> {
            if (result.success) {
                SuggestionGame suggestionGame = (SuggestionGame) result.result;
                if (suggestionGame == null) {
                    return;
                }

                if (suggestionGame.canceled) {
                    return;
                } else if (!suggestionGames.containsKey(suggestionGame.uid) && !suggestionGame.canceled) {
                    suggestionGames.put(suggestionGame.uid, suggestionGame);
                    if (updateListener != null) {
                        updateListener.onOnSuggestionGamesChange();
                    }
                } else {
                    suggestionGames.get(suggestionGame.uid).copy(suggestionGame);
                    if (updateListener != null) {
                        updateListener.onOnSuggestionGameUpdate(suggestionGame);
                    }
                }

                PreferencesManager.getInstance().saveSuggestionGames(suggestionGames);
            }
        };
    }

    private void loadSuggestionGamesThread() {
        suggestionGamesLoadingThread = new Thread(() -> {
            while (loadingSuggestionGamesStatus != e_loadingStatus.success) {
                FirebaseManager.getInstance().loadSuggestionGames(result -> {
                    if (result.success) {
                        if (loadingSuggestionGamesStatus == e_loadingStatus.success) {
                            return;
                        }

                        loadingSuggestionGamesStatus = e_loadingStatus.success;
                        suggestionGames = new SuggestionGames((SuggestionGame[]) result.result);

                        PreferencesManager.getInstance().saveSuggestionGames(suggestionGames);
                        if (updateListener != null) {
                            UIUtils.runOnUI(() -> {
                                updateListener.onOnSuggestionGamesChange();
                            });
                        }
                        registerToSuggestionGameUpdate();
                        suggestionGamesLoadingThread = null;
                    } else {
                        if (loadingSuggestionGamesStatus == e_loadingStatus.success) {
                            return;
                        }

                        loadingSuggestionGamesStatus = e_loadingStatus.error;
                        if (updateListener != null) {
                            UIUtils.runOnUI(() -> {
                                updateListener.onError(result.error);
                            });
                        }
                    }
                });

                Utils.sleep(30000);
            }
        });
        suggestionGamesLoadingThread.setPriority(Thread.MIN_PRIORITY);
        suggestionGamesLoadingThread.start();
    }

    public SuggestionGame[] getSuggestionGames() {
        if (suggestionGames == null) {
            return null;
        }

        return suggestionGames.asArray();
    }

    public void addSuggestionGame(SuggestionGame suggestionGame, ActionListener listener) {
        FirebaseManager.getInstance().addSuggestionGame(suggestionGame, result -> {
            if (result.success) {
                SuggestionGame newSuggestionGame = (SuggestionGame) result.result;
                if (!suggestionGames.containsKey(newSuggestionGame.uid)) {
                    suggestionGames.put(newSuggestionGame.uid, newSuggestionGame);
                }
            }

            listener.onResult(result);
        });
    }
    // endregion


    // region sudoku
    //
    ArrayList<DataSource> sudokuGamesDataSource;
    private void readSudokuSamples() {
        sudokuGamesDataSource=new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(AppBase.getContext().getAssets().open("sudoku.csv")));

            String line;
            reader.readLine();
            int index = 0;
            while ((line = reader.readLine()) != null) {
                addSudokuDataSource(index++, line);
            }
        } catch (IOException e) {
           Log.e("DataSource","error",e);
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("DataSource","error",e);
                }
            }
        }
    }

     private void addSudokuDataSource(int id, String data) {
        String[] array = data.split(",");
        DataSource dataSource = new DataSource();
        dataSource.id = id;
        dataSource.baseValues = new int[9][9];
        dataSource.values = new int[9][9];

        for (int i=0;i<array[0].length();i++) {
            char c = array[0].toCharArray()[i];
            int val=Utils.getInt(c+"");
            if(val>0) {
                dataSource.level++;
            }
            dataSource.baseValues[i / 9][i %9]=val;
        }
        for (int i=0;i<array[1].length();i++) {
            char c = array[1].toCharArray()[i];
            int val=Utils.getInt(c+"");
            dataSource.values[i / 9][i %9]=val;
        }
        sudokuGamesDataSource.add(dataSource);
    }
    public DataSource getSudokuDataSource(int id) {
        return sudokuGamesDataSource.get(id);
    }
    // endregion
}
