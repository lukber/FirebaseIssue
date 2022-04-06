package cz.datasys.firebaseissue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
	private MovieAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FirebaseFirestore.setLoggingEnabled(true);

		mFirestore = FirebaseFirestore.getInstance();

		mAdapter = new MovieAdapter();

		mListView = findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener((adapterView, view, index, l) -> {

		});

		findViewById(R.id.add).setOnClickListener(view -> {
			Map<String, Object> user = new HashMap<>();
			user.put("name", "Star wars (episode " + mMovies.size() + ")");
			user.put("likes", 0);
			user.put("stars", 0);

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
					mAdapter.notifyDataSetChanged();
				});
	}

	static class Movie {

		private String id;
		private String name;
		private int likes;
		private int stars;

		public Movie() {
		}

		public Movie(String name, int likes, int stars) {
			this.name = name;
			this.likes = likes;
			this.stars = stars;
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

		public int getStars() {
			return stars;
		}

		public void setStars(int stars) {
			this.stars = stars;
		}

		@Override
		public String toString() {
			return "Movie{" +
					"id='" + id + '\'' +
					", name='" + name + '\'' +
					", likes=" + likes +
					", stars=" + stars +
					'}';
		}
	}

	class MovieAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mMovies.size();
		}

		@Override
		public Object getItem(int i) {
			return mMovies.get(i);
		}

		@Override
		public long getItemId(int i) {
			return 0;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_movie, viewGroup, false);
			}

			final Movie movie = (Movie) getItem(i);
			((TextView) view.findViewById(R.id.text)).setText(movie.toString());

			view.findViewById(R.id.likes).setOnClickListener(view1 -> {
				//increment likes
				mFirestore.collection("movies")
						.document(movie.id)
						.update("likes", movie.likes + 1);
			});
			view.findViewById(R.id.stars).setOnClickListener(view1 -> {
				//increment stars
				mFirestore.collection("movies")
						.document(movie.id)
						.update("stars", movie.stars + 1);
			});

			return view;
		}
	}
}