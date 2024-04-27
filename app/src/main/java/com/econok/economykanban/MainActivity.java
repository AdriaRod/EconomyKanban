package com.econok.economykanban;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardItem> cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cardList = new ArrayList<>();
        cardList.add(new CardItem("Título 1", "Income", "Comida","342"));
        cardList.add(new CardItem("Título 2", "Expense", "Viaje","213"));
        cardList.add(new CardItem("Título 3", "Income", "Comida","534"));
        cardList.add(new CardItem("Título 4", "Expense", "Comida","432"));
        cardList.add(new CardItem("Título 5", "Income", "Viaje","123"));
        cardList.add(new CardItem("Titulo 6","Income","Comida","293"));

        adapter = new CardAdapter(this, cardList);
        recyclerView.setAdapter(adapter);
    }
}
