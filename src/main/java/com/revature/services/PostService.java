package com.revature.services;

import com.revature.models.Post;
import com.revature.models.Profile;
import com.revature.models.User;
import com.revature.repositories.CommentRepository;
import com.revature.repositories.PostRepository;
import com.revature.repositories.ProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class PostService {
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final ProfileRepository profileRepository;

	@Autowired
	public PostService(PostRepository postRepository, CommentRepository commentRepository,
			ProfileRepository profileRepository) {
		this.postRepository = postRepository;
		this.commentRepository = commentRepository;
		this.profileRepository = profileRepository;
		
	}

	public List<Post> getPosts() {
		List<Post> allPosts = postRepository.findAll();
//		Collections.reverse(allPosts);
		return allPosts;
	}

	public Post addNewPost(Post post, User user)
    {
        post.setAuthor(user);
        Optional<Profile> optProfile = profileRepository.getProfileByUser(user);
        Profile profile = null;
        if(!optProfile.isPresent()) {
        	profile = new Profile();
        	profile.setUser(user);
        	profileRepository.save(profile);
        	System.out.println("Generating default profile! (Probably should never happen...)");
        } else {
        	profile = optProfile.get();
        }
        Date date = new Date();
        DateFormat dform = new SimpleDateFormat("MMM d yyyy HH:mm z");
        dform.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String dateString = dform.format(date);
        post.setDate(dateString);
        post.setProfile(profile);
        return postRepository.save(post);
    }

	public void deletePost(Post post) {
		Optional<Post> temp = postRepository.findById(post.getId());

		if (temp.isPresent()) {
			for (int i = 0; i < temp.get().getComments().size(); i++) {
				commentRepository.deleteById(temp.get().getComments().get(i).getId());
			}

			postRepository.deleteById(temp.get().getId());
			System.out.println("post deleted");
		} else
			throw new IllegalStateException("post does not exist");
	}

	public List<Post> getUserPosts(String authorUID) {
		List<Post> ret = new ArrayList<Post>();
		for (Post p : postRepository.findAll()) {
			if (p.getAuthor().getUid().equals(authorUID)) {
				ret.add(p);
			}
		}
		return ret;
	}
}
