package cz.datasys.firebaseissue;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

	public static final String TAG = "APP_LOG";

	FirebaseFirestore mFirestore;

	ListView mListView;

	List<Movie> mMovies = new LinkedList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FirebaseFirestore.setLoggingEnabled(true);

		mFirestore = FirebaseFirestore.getInstance();
		mListView = findViewById(R.id.list);
		mListView.setOnItemClickListener((adapterView, view, index, l) -> {
			Movie movie = mMovies.get(index);
			movie.likes += 1;
			mFirestore.collection("movies")
					.document(movie.id)
					.update("likes", movie.likes);
		});

		findViewById(R.id.add).setOnClickListener(view -> {
			Map<String, Object> user = new HashMap<>();
			user.put("name", "Star wars (episode " + mMovies.size() + ")");
			user.put("likes", 0);

			// Add a new document with a generated ID

			mFirestore.collection("movies")
					.add(user)
					.addOnSuccessListener(documentReference -> Log.d(TAG, "Movie added with ID: " + documentReference.getId()))
					.addOnFailureListener(e -> Log.w(TAG, "Error adding movie", e));

		});

		mFirestore.collection("movies")
				.addSnapshotListener((snapshots, e) -> {
					if (e != null) {
						Log.w(TAG, "listen:error", e);
						return;
					}

					mMovies.clear();
					for (DocumentSnapshot snapshot : snapshots.getDocuments()) {
						String item = "Snapshot: " + snapshot.getData() + ", ID: " + snapshot.getId();
						Log.d(TAG, item);

						Movie movie = snapshot.toObject(Movie.class);
						movie.setId(snapshot.getId());
						mMovies.add(movie);
					}
					ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, mMovies);
					mListView.setAdapter(arrayAdapter);
				});
	}

	static class Movie {

		private String id;
		private String name;
		private int likes;

		public Movie() {
		}

		public Movie(String name, int likes) {
			this.name = name;
			this.likes = likes;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getLikes() {
			return likes;
		}

		public void setLikes(int likes) {
			this.likes = likes;
		}

		@Override
		public String toString() {
			return "Movie{" +
					"id='" + id + '\'' +
					", name='" + name + '\'' +
					", likes=" + likes +
					'}';
		}
	}
}