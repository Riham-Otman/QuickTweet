import { Dispatch, SetStateAction, useRef, useState } from "react";
import { FilterProps, Post as PostInterface } from "../types";

function useInitialValue(value: PostInterface[]): PostInterface[] {
  const initialValueRef = useRef<PostInterface[]>(value);

  // Return the stored initial value
  return initialValueRef.current;
}

const Filters = ({ posts, setPosts }: FilterProps): React.ReactElement => {
  const [textInp, setText] = useState("");
  const [userInp, setUser] = useState("");
  const [interestInp, setInterest] = useState("");
  const initialPosts = useInitialValue(posts);

  const filter = (
    e: React.FormEvent<HTMLFormElement>,
    text: string,
    user: string,
    interestSearch: string,
    setPosts: Dispatch<SetStateAction<PostInterface[]>>
  ) => {
    e.preventDefault();

    // partial search for posts using the specified content string, username and/or interests
    let postTemp: PostInterface[];
    postTemp = initialPosts.filter(post => post.content.toLowerCase().includes(text.toLowerCase()));
    postTemp = postTemp.filter(post => {
      if (post.user.username == undefined) {
        return false;
      }

      return post.user.username.toLowerCase().includes(user.toLowerCase());
    });
    postTemp = postTemp.filter(post =>
      post.user.interests?.some(interest =>
        interest.toLowerCase().includes(interestSearch.toLowerCase())
      )
    );

    setPosts(postTemp);
  };

  return (
    <form
      style={styles.container}
      onSubmit={e => filter(e, textInp, userInp, interestInp, setPosts)}
    >
      <div style={styles.item}>
        <label>
          <p>Post Contains:</p>
          <input type='text' id='text' value={textInp} onChange={e => setText(e.target.value)} />
        </label>
      </div>
      <div style={styles.item}>
        <label>
          <p>Username:</p>
          <input type='text' id='user' value={userInp} onChange={e => setUser(e.target.value)} />
        </label>
      </div>
      <div style={styles.item}>
        <label>
          <p> User Interests:</p>
          <input
            type='text'
            id='interest'
            value={interestInp}
            onChange={e => setInterest(e.target.value)}
          ></input>
        </label>
      </div>
      <div style={styles.item}>
        <input style={styles.submit} type='submit' value='Submit'></input>
      </div>
    </form>
  );
};

const styles: {
  container: React.CSSProperties;
  item: React.CSSProperties;
  submit: React.CSSProperties;
} = {
  container: {
    display: "flex",
    justifyContent: "space-between", // Distribute items evenly
    alignItems: "center", // Align items vertically centered
    padding: "10px",
    backgroundColor: "#0d6efd",
    boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
    zIndex: 1000,
    position: "fixed",
    top: "0", // adjust based on where you want it
    left: "0",
    right: "0",
    margin: "0 auto",
  },
  item: {
    color: "white",
    flex: "1 1 0", // Allow items to grow and shrink equally
    textAlign: "center",
    padding: "10px",
    margin: "5px",
  },
  submit: {
    color: "#0d6efd",
    backgroundColor: "white",
    border: "6px double #0d6efd",
  },
};

export default Filters;
