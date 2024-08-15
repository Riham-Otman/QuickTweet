import axios from "axios";
import { User } from "../types";

export const fetchPosts = async (user: User) => {
  const token = sessionStorage.getItem("jwt");
  try {
    const res = await axios.get(
      `${import.meta.env.VITE_API_URL}/posts/${user.username}`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: token,
        },
      }
    );
    return res.data;
  } catch (err) {
    console.error(err);
  }
};

export const createPost = async (user: User, content: string) => {
  const token = sessionStorage.getItem("jwt");
  try {
    const res = await axios.post(
      `${import.meta.env.VITE_API_URL}/posts/${user.username}`,
      { content: content },
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: token,
        },
      }
    );
    return res.data;
  } catch (err) {
    console.error(err);
  }
};
