import { Dispatch, FC, SetStateAction } from "react";
import { NavigateFunction } from "react-router";


export interface User {
  id?: number;
  username: string | undefined;
  email: string | undefined;
  password: string | undefined;
  bio: string | undefined;
  role: string | undefined;
  photo: string | undefined;
  status: string | undefined;
  friends: User[] | undefined;
  friendRequests: User[];
  interests: string[] | undefined;
}

export interface SearchResult {
  value: string;
  label: string;
  path: string;
}

export const initialUser: User = {
  username: "",
  email: "",
  password: "",
  bio: "",
  role: "",
  photo: "",
  status: "",
  friends: [],
  friendRequests: [],
  interests: [],
};

export interface Interest {
  id: number;
  title: string;
  users: User[];
}

export interface Post {
  id: number;
  content: string;
  createdDate: Date;
  user: User;
}

export interface Group {
  id: number;
  name: string;
  members: User[];
}

export interface LoginFormProps {
  setAuth: Dispatch<SetStateAction<boolean>>;
  user: User;
  setUser: Dispatch<SetStateAction<User>>;
}

export interface FormComponents {
  setAuth: Dispatch<SetStateAction<boolean>>;
  user: User;
  setUser: Dispatch<SetStateAction<User>>;
}

export interface FormProps {
  Form: FC<FormComponents>;
  formComponents: FormComponents;
}

export interface UsernameFormProps {
  navigate: NavigateFunction;
  setUsername: Dispatch<SetStateAction<string>>;
  setSecurityQuestion: Dispatch<SetStateAction<string>>;
  setErr: Dispatch<SetStateAction<string>>;
  setShow: Dispatch<SetStateAction<boolean>>;
}

export interface NavBarProps {
  username: string | undefined;
  setAuth: Dispatch<SetStateAction<boolean>>;
  isAdmin?: boolean;
  user?: User;
}

export interface PostFormProps {
  modal: boolean;
  setModal: Dispatch<SetStateAction<boolean>>;
  user: User;
  setLoading: Dispatch<SetStateAction<boolean>>;
}

export interface FilterProps {
  posts: Post[]; 
  setPosts: Dispatch<SetStateAction<Post[]>>;
}

export interface UserData {
  username: string;
  bio: string | undefined;
  status: string | undefined;
  photo: string | undefined;
  interests: string[] | undefined;
}

export interface UserProfileProps {
  currentUsername: string | undefined;
}

export interface FeedProps {
  user: User;
  setAuth: Dispatch<SetStateAction<boolean>>;
}

export interface GroupPageProps {
  user: User;
  interest: string | undefined;
}
