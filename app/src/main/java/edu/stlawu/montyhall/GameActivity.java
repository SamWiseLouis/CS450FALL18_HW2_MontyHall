//author Samuel Emerson
// citations

//hello

package edu.stlawu.montyhall;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.CountDownTimer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.BreakIterator;
import java.util.Arrays;


import java.util.Random;

import static edu.stlawu.montyhall.MainFragment.PREF_NAME;

public class GameActivity extends AppCompatActivity {
    public Random randomizer = new Random();

    public ImageButton doorOne = null; // make each door a Image button so i can add onclick
    public ImageButton doorTwo = null;
    public ImageButton doorThree = null;

    //check to see if a win door saved
    private ImageButton winDoor = null;
    //check to see if door selected
    private ImageButton selectedDoor = null;
    private  ImageButton goatDoor = null;

    private TextView prompt = null;
    private TextView again = null;

    // get the wins and losses
    //and index of selected and win door
    private int win = 0;
    private int loss = 0;
    private int index_selected_door = -1;   //set  both to -1 in case not set
    private int index_winning_door = -1;
    private int index_revealed_door = -1;

   //textViews
    private TextView win_cnt = null;
    private TextView loss_cnt = null;

    //sound stuff
    public AudioAttributes aa = null;
    private SoundPool soundPool = null;
    private int click_door = 0;
    private int carSound = 0;
    private int goatSound = 0;
    //check if new game or continue game
    private boolean newGame;

    // door string stuff
    private ImageButton doors[];


    //easy function to save/ update save data
    private  void saveData(){
        //here i shall save the win door and stuff
        getPreferences(MODE_PRIVATE)
                .edit()
                .putInt("win_Door", index_selected_door)
                .apply();

        getPreferences(MODE_PRIVATE)
                .edit()
                .putInt("goat_door", index_revealed_door)
                .apply();

        getPreferences(MODE_PRIVATE)
                .edit()
                .putInt("selected_door", index_selected_door)
                .apply();
        getPreferences(MODE_PRIVATE)
                .edit()
                .putInt("win_count", win)
                .apply();
        getPreferences(MODE_PRIVATE)
                .edit()
                .putInt("loss_count", loss)
                .apply();
    }


    private void reveal(ImageButton button){
        Animator anim = AnimatorInflater
                .loadAnimator(GameActivity.this,
                        R.animator.slide_in);
        anim.setTarget(button);
        anim.start();
    }
    private void rotate(ImageButton button){
        float deg = button.getRotation() + 360F;
        button.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    // generates a new win door
    private ImageButton generate_win(){
        int win = (randomizer.nextInt(3)); // choose one number from 1-3 for the car to be behind
        index_winning_door = win; // update winning door index
        ImageButton aDoor = doors[win];
        System.out.println("the win door is: "+ aDoor.getId());
        return aDoor;
    }




    //clicked door function
    private void Image_button_clicked(final ImageButton aButton) {
        System.out.println("click"); soundPool.play(click_door, 1f,
                1f, 1, 0, 1f);

        //check if first time clicking a door or if switching
        if (selectedDoor == null) {

            selectedDoor = aButton;

            index_selected_door = Arrays.asList(doors).indexOf(aButton);
            //immediatly save the selected door index
            getPreferences(MODE_PRIVATE)
                    .edit()
                    .putInt("selected_door", index_selected_door)
                    .apply();

            reveal(aButton);
            prompt.setText("Switch Door?");
            //System.out.println("selected door: " + selectedDoor);
            aButton.setImageResource(R.drawable.closed_door_chosen);
            //must reveal a door with a goat randomly
            int tmp = randomizer.nextInt(3);
            while (doors[tmp] == winDoor || doors[tmp] == selectedDoor) {
                tmp =randomizer.nextInt(3);
            }
            System.out.println("revealing goat door: "+ tmp);
            ImageButton aDoor = doors[tmp];
            aDoor.setImageResource(R.drawable.goat);
            index_revealed_door = tmp;
            goatDoor= aDoor;
            getPreferences(MODE_PRIVATE)
                    .edit()
                    .putInt("goat_door", index_revealed_door)
                    .apply();

        }
        // check to see if new game needed
        else{
            //disable the buttons while it runs animation
            doorOne.setEnabled(false);
            doorTwo.setEnabled(false);
            doorThree.setEnabled(false);

            selectedDoor.setImageResource(R.drawable.door); //change selected door to regular door
            //delay for three animations of countdown to show before win or loose
            //timer stuff
            reveal(aButton);
            BreakIterator mTextField;
            new CountDownTimer(1000, 100) {

                public void onTick(long millisUntilFinished) {

                    aButton.setImageResource(R.drawable.three);
                }

                public void onFinish() {
                    reveal(aButton);
                    BreakIterator mTextField;
                    new CountDownTimer(1000, 100) {

                        public void onTick(long millisUntilFinished) {

                            aButton.setImageResource(R.drawable.two);
                        }

                        public void onFinish() {
                            reveal(aButton);
                            BreakIterator mTextField;
                            new CountDownTimer(1000, 100) {

                                public void onTick(long millisUntilFinished) {

                                    aButton.setImageResource(R.drawable.one);
                                }

                                public void onFinish() {

                                    //win case
                                    if (winDoor == aButton){
                                        aButton.setImageResource(R.drawable.car);
                                        prompt.setText("You WIN");
                                        win = win+1;

                                        soundPool.play(carSound, 1f,
                                                1f, 1, 0, 1f);
                                    } else {
                                        //lose case
                                        aButton.setImageResource(R.drawable.goat);
                                        prompt.setText("You Loose");

                                        loss = loss +1;
                                        winDoor.setImageResource(R.drawable.car);
                                        soundPool.play(goatSound, 1f,
                                                1f, 1, 0, 1f);
                                        doorOne.setEnabled(false);
                                        doorTwo.setEnabled(false);
                                        doorThree.setEnabled(false);
                                        float deg = aButton.getRotation() + 360F;
                                        rotate(aButton);

                                    }
                                    //update the stats and ask if they want to play again

                                    index_selected_door= -1;
                                    index_winning_door = -1;
                                    index_revealed_door = -1;
                                    winDoor = null;
                                    selectedDoor =null;
                                    goatDoor = null;
                                    loss_cnt.setText(Integer.toString(loss));
                                    win_cnt.setText(Integer.toString(win));
                                    again.setText("Play again?");
                                    again.setEnabled(true);
                                    again.setVisibility(View.VISIBLE);
                                    saveData();

                                }
                            }.start();
                        }
                    }.start();
                }
            }.start();
        }
    }






  @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //new game stuff

      //check to see if new game
      this.newGame = getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE).getBoolean("NEWCLICKED",true);

      System.out.println("is it a new game:  "+newGame);


      //get wins and losses
      this.win = 0;
      this.loss = 0;
      this.win_cnt = findViewById(R.id.win_text);
      this.loss_cnt = findViewById(R.id.loss_text);
      this.prompt = findViewById(R.id.prompt);
      this.again = findViewById(R.id.again);



      //Sound stuff
      this.aa = new AudioAttributes
              .Builder()
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .setUsage(AudioAttributes.USAGE_GAME)
              .build();
      this.soundPool = new SoundPool.Builder()
              .setMaxStreams(1)
              .setAudioAttributes(aa)
              .build();
      this.click_door = this.soundPool.load(
              this, R.raw.click_door, 1);

      this.carSound = this.soundPool.load(
              this, R.raw.car, 1);
      this.goatSound = this.soundPool.load(
              this,R.raw.bleat,1);

      selectedDoor = null;
      doorOne =(ImageButton)findViewById(R.id.door1);
      doorTwo =(ImageButton)findViewById(R.id.door2);
      doorThree =(ImageButton)findViewById(R.id.door3);

      doors = new ImageButton[]{doorOne,doorTwo,doorThree}; // array of the location of door strings


      this.doorOne.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Image_button_clicked(doorOne);

          }
      });
      this.doorTwo.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Image_button_clicked(doorTwo);
          }
      });
      this.doorThree.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Image_button_clicked(doorThree);
          }
      });
      // in case this is first time and we need to generate a win door


        if(newGame == true){
            System.out.println("on create, new game button clicked erasing old data");
            this.win = 0;
            this.loss = 0;
            getPreferences(MODE_PRIVATE).edit().putInt("win_count",0);
            getPreferences(MODE_PRIVATE).edit().putInt("loss_count",0);
            this.selectedDoor = null;
            index_selected_door = -1;
            System.out.println("starting over the selected door is: "+selectedDoor);
            winDoor = generate_win();
            getPreferences(MODE_PRIVATE).edit().putInt("win_Door",-1);
            //set doors to original state

        }else{
            System.out.println("on create, continue button clicked, loading save data");
          //load saved data
            System.out.println(getPreferences(MODE_PRIVATE).contains("win_Door"));
            index_winning_door = getPreferences(MODE_PRIVATE).getInt("win_Door",-1);
            System.out.println("The stored win index is : "+ index_winning_door);

            if (index_winning_door == -1){
                winDoor = generate_win(); //generate a new win door
            }else {
                winDoor = doors[index_winning_door]; //set the stored win door as the new win
            }
            //goat door stuff
            index_revealed_door = getPreferences(MODE_PRIVATE).getInt("goat_door",-1);

            if(index_revealed_door == -1){
                goatDoor = null;
            }else{
                goatDoor = doors[index_revealed_door];
                goatDoor.setImageResource(R.drawable.goat);
            }

            index_selected_door = getPreferences(MODE_PRIVATE).getInt("selected_door",-1);
            System.out.println("The stored selected index: "+ index_selected_door);
            if (index_selected_door == -1){
                selectedDoor = null;
            }else {

                selectedDoor =doors[index_selected_door];
                selectedDoor.setImageResource(R.drawable.closed_door_chosen);
            }

            win = getPreferences(MODE_PRIVATE).getInt("win_count",0);  // no clue why this does not work
            System.out.println("The stored win count is : "+ win);
            System.out.println("string of win count: "+ Integer.toString(win));

            loss = getPreferences(MODE_PRIVATE).getInt("loss_count",0);
            System.out.println("The stored loss count is : "+ loss);

            loss_cnt.setText(Integer.toString(loss));
            win_cnt.setText(Integer.toString(win));

        }

        //simple button to reset the game
      //disappears after clicked until game ends
      again.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              selectedDoor = null;
              goatDoor = null;
              winDoor = generate_win();
              index_revealed_door = -1;
              index_selected_door = -1;
              index_winning_door = -1;
              doorOne.setImageResource(R.drawable.door);
              doorTwo.setImageResource(R.drawable.door);
              doorThree.setImageResource(R.drawable.door);
              again.setText("");
              prompt.setText("Choose a Door");
              doorOne.setEnabled(true);
              doorTwo.setEnabled(true);
              doorThree.setEnabled(true);
              again.setVisibility(View.INVISIBLE);
              saveData();

          }
      });



    }

    @Override
    protected void onStart() {
        saveData();
        super.onStart();
    }

    @Override
    protected void onPause() {
        //save everything
        saveData();
        super.onPause();
    }

    @Override
    protected void onStop() {
        saveData();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        saveData();
        //save stuff so that game can be continued
        super.onDestroy();
    }


}

