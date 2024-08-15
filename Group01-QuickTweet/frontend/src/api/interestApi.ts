import axios from "axios";
import { getUserData } from "./userApi";

export const getUsersWithInterest = async (interest: string | undefined) => {
    const token = sessionStorage.getItem("jwt");
    if (interest === undefined) {
        return;
    }
    try{
        const usersWithInterest = await axios.post(
            `${import.meta.env.VITE_API_URL}/users/interests`, 
            [interest],
            {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: token,
                },
            }
        );
        return usersWithInterest.data;
    } catch (err) {
        console.error(err);
    }
};

export const countUsersWithInterest = async (interest: string | undefined) => {
    if (interest === undefined) {
        return;
    }
    try{
        const usersWithInterest = await getUsersWithInterest(interest);
        return usersWithInterest.length;
    } catch (err) {
        console.error(err);
    }
};

export const checkUserInterest = async (username: string | undefined, interest: string| undefined)=> {
    if (interest === undefined || username == undefined) {
        return;
    }
    const user = await getUserData(username);
    return user.interests.includes(interest);
};

