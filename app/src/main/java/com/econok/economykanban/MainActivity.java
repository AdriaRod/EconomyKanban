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
        cardList.add(new CardItem("Título 1", "Etiqueta 1", 100));
        cardList.add(new CardItem("Título 2", "Etiqueta 2", 200));
        cardList.add(new CardItem("Título 3", "Etiqueta 3", 300));
        cardList.add(new CardItem("Título 4", "Etiqueta 4", 400));
        cardList.add(new CardItem("Título 5", "Etiqueta 5", 500));

        adapter = new CardAdapter(this, cardList);
        recyclerView.setAdapter(adapter);
    }
}
