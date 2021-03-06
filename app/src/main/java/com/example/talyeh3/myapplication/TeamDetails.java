package com.example.talyeh3.myapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TeamDetails extends AppCompatActivity implements View.OnClickListener{
    TextView tvName,btnAddPlayer;
    ImageView btnTeamPlayers;
    FirebaseDatabase database;
    DatabaseReference teamRef;
    ImageView user_profile_photo;
    Team t;
    String key;
    String photo="";


    Dialog d;
    int firstPress=0;
    ListView lv;
    int i = 0;
    private DatabaseReference database2,teamDatabase;
    ArrayList<User> users;
    String keyUser="";
    AllUsersAdapter allPlayersAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);//try commit
        Toast.makeText(TeamDetails.this, "sd  "+firstPress,Toast.LENGTH_SHORT).show();
        user_profile_photo=(ImageView)findViewById(R.id.user_profile_photo);
        database = FirebaseDatabase.getInstance();
        tvName = (TextView) findViewById(R.id.tvName);
        btnAddPlayer=(TextView)findViewById( R.id.btnAddPlayer );
        btnTeamPlayers=(ImageView) findViewById( R.id.btnTeamPlayers );
        btnAddPlayer.setOnClickListener( this );
        btnTeamPlayers.setOnClickListener( this );


        Intent intent = getIntent();
        key = intent.getExtras().getString("keyteam");
        teamRef = database.getReference("Teams/" + key);
        this.retrieveData();

        //for all players team
        database2 = FirebaseDatabase.getInstance().getReference("Teams/"+key+"/users");
    }



    public void teamPlayers()
    {
        if(firstPress==0)
        {
            d= new Dialog(this);
            d.setContentView(R.layout.activity_all_users);
            d.setCancelable(true);
            lv = (ListView) d.findViewById(R.id.lv);
            this.retriveDataPlayers();
            d.show();
            Toast.makeText(TeamDetails.this, "a"+firstPress,Toast.LENGTH_SHORT).show();
        }
        else
            {
            d.show();
                Toast.makeText(TeamDetails.this, "b",Toast.LENGTH_SHORT).show();
        }
        d.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    d.dismiss();
                    firstPress=1;
                }
                return true;
            }
        });
    }


    public void retrieveData()
    {
        teamRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                t = dataSnapshot.getValue(Team.class);
                tvName.setText("team name: " + t.name);
                photo= t.imgUrl;
                        Picasso
                        .with( TeamDetails.this )
                        .load( photo)
                        .fit() // will explain later
                        .into(user_profile_photo );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void retriveDataPlayers() {
        database2.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                users = new ArrayList<User>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    keyUser = (String) snapshot.child(String.valueOf(i)).getValue();//put in array of users at the teams key and not mail
                    //Toast.makeText(TeamDetails.this, "sd  "+firstPress,Toast.LENGTH_SHORT).show();
                    teamDatabase = FirebaseDatabase.getInstance().getReference("Users/" + keyUser);



                    ValueEventListener valueEventListener = teamDatabase.addValueEventListener(new ValueEventListener() {
                        public void onDataChange(DataSnapshot snapshot) {
                            User u = snapshot.getValue(User.class);
                            users.add(u);
                            snapshot.toString();
                            allPlayersAdapter.notifyDataSetChanged();
                        }

                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

                    allPlayersAdapter = new AllUsersAdapter(TeamDetails.this, 0, 0, users);
                    lv.setAdapter(allPlayersAdapter);

                    i++;

                }

            }
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void onClick(View v) {
        if (v==btnAddPlayer)
        {
            Intent intent = new Intent( TeamDetails.this, OpenTeam.class );
            intent.putExtra( "teamKey", t.key );
            startActivity( intent );
        }
        if(v==btnTeamPlayers)
        {
            teamPlayers();
        }

    }



}
