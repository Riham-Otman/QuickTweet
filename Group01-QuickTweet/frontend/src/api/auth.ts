import axios from "axios";

export const updateStatus = async (
  username: string | undefined,
  status: string
) => {
  const token = sessionStorage.getItem("jwt");
  try {
    const res = await axios.put(
      `${import.meta.env.VITE_API_URL}/users/status/${username}`,
      status,
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
