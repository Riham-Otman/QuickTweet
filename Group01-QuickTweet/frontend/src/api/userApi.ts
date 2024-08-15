import axios from "axios";
import { User, UserData } from "../types";

export const getAllUsers = async (): Promise<User[]> => {
  const token = sessionStorage.getItem("jwt");
  const response = await axios.get(`${import.meta.env.VITE_API_URL}/users`, {
    headers: {
      "Content-Type": "application/json",
      Authorization: token,
    },
  });
  return response.data as User[];
};

export const getUserData = async (username: string | undefined) => {
  if (username === undefined) {
    return;
  }

  const token = sessionStorage.getItem("jwt");
  try {
    const res = await axios.get(`${import.meta.env.VITE_API_URL}/users/username/${username}`, {
      headers: {
        "Content-Type": "application/json",
        Authorization: token,
      },
    });
    return res.data;
  } catch (err) {
    console.error(err);
  }
};

export const postUserData = async (userObj: UserData | User, username: string | undefined) => {
  if (username === undefined) {
    return;
  }

  const token = sessionStorage.getItem("jwt");
  try {
    const res = await axios.put(`${import.meta.env.VITE_API_URL}/users/${username}`, userObj, {
      headers: {
        "Content-Type": "application/json",
        Authorization: token,
      },
    });

    return res.data;
  } catch (err) {
    console.error(err);
  }
};

export const getFriendRequests = async (username: string | undefined) => {
  const token = sessionStorage.getItem("jwt");
  try {
    const res = await axios.get(
      `${import.meta.env.VITE_API_URL}/users/friends/requests/${username}`,
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

export const getFriends = async (username: string | undefined) => {
  const token = sessionStorage.getItem("jwt");
  try {
    const res = await axios.get(`${import.meta.env.VITE_API_URL}/users/friends/${username}`, {
      headers: {
        "Content-Type": "application/json",
        Authorization: token,
      },
    });
    return res.data;
  } catch (err) {
    console.error(err);
  }
};

export const addFriendRequest = async (
  username: string | undefined,
  friendUsername: string | undefined
) => {
  const token = sessionStorage.getItem("jwt");
  try {
    const res = await axios.put(
      `${import.meta.env.VITE_API_URL}/users/friends/requests/${username}`,
      friendUsername,
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

export const addFriend = async (
  username: string | undefined,
  friendUsername: string | undefined
) => {
  const token = sessionStorage.getItem("jwt");
  try {
    const res = await axios.put(
      `${import.meta.env.VITE_API_URL}/users/friends/${username}`,
      friendUsername,
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

export const deleteFriend = async (
  username: string | undefined,
  friendUsername: string | undefined
) => {
  const token = sessionStorage.getItem("jwt");
  try {
    const res = await axios.post(
      `${import.meta.env.VITE_API_URL}/users/friends/${username}`,
      friendUsername,
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

export const createUserRequest = async (user: any) => {
  const res = await axios.post(`${import.meta.env.VITE_API_URL}/users`, user, {
    headers: { "Content-Type": "application/json" },
  });
  return res.data;
};

export const getPendingRequests = async (username: string): Promise<User[]> => {
  const token = sessionStorage.getItem("jwt");
  const res = await axios.get(`${import.meta.env.VITE_API_URL}/admin/requests/${username}`, {
    headers: { "Content-Type": "application/json", Authorization: token },
  });
  return res.data as User[];
};

export const deleteUserAccount = async (id: number): Promise<User> => {
  const token = sessionStorage.getItem("jwt");
  const res = await axios.delete(`${import.meta.env.VITE_API_URL}/users/${id}`, {
    headers: { "Content-Type": "application/json", Authorization: token },
  });
  return res.data;
};

export const acceptUserRequest = async (username: string): Promise<string> => {
  const token = sessionStorage.getItem("jwt");
  const res = await axios.put(
    `${import.meta.env.VITE_API_URL}/admin/requests/${username}`,
    {},
    {
      headers: { "Content-Type": "application/json", Authorization: token },
    }
  );
  return res.data;
};

export const rejectUserRequest = async (username: string): Promise<string> => {
  const token = sessionStorage.getItem("jwt");
  const res = await axios.delete(`${import.meta.env.VITE_API_URL}/admin/requests/${username}`, {
    headers: { "Content-Type": "application/json", Authorization: token },
  });

  return res.data;
};

export const changeUserRole = async (id: number, adminUsername: string): Promise<string> => {
  const token = sessionStorage.getItem("jwt");
  const res = await axios.put(`${import.meta.env.VITE_API_URL}/admin/users/${id}`, adminUsername, {
    headers: { "Content-Type": "application/json", Authorization: token },
  });

  return res.data;
};

export const getSecurityQuestion = async (username: string) => {
  const res = await axios.get(`${import.meta.env.VITE_API_URL}/users/forgotPassword/${username}`);
  return res.data;
};

export const updatePassword = async (
  username: string,
  { password, answer }: { password: string; answer: string }
) => {
  const res = await axios.post(
    `${import.meta.env.VITE_API_URL}/users/forgotPassword/${username}`,
    { password, answer },
    {
      headers: { "Content-Type": "application/json" },
    }
  );
  return res.data;
};

export const searchUsers = async (query: string): Promise<User[]> => {
  const token = sessionStorage.getItem("jwt");
  try {
    const response = await axios.get(
      `${import.meta.env.VITE_API_URL}/users/search?query=${query}`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: token,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error searching users:", error);
    return [];
  }
};
