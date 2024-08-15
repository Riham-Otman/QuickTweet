import axios from "axios";
import React, { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import ErrorToast from "./ErrorToast";
import { LoginFormProps } from "../types";
import { updateStatus } from "../api/auth";

/**
 * LoginForm component provides a form for users to log in. It captures user input for username and password,
 * and attempts to authenticate the user via an API call. On successful login, it stores the JWT token in sessionStorage,
 * updates the user's status to "Available", and navigates to the user's dashboard. In case of an error during login,
 * it displays an error message using the ErrorToast component.
 *
 * @component
 * @param {LoginFormProps} props - The props for the LoginForm component.
 * @param {Dispatch<SetStateAction<boolean>>} props.setAuth - Function to update the authentication state in the parent component.
 * @param {User} props.user - The user object to be updated with form inputs.
 * @param {Dispatch<SetStateAction<User>>} props.setUser - Function to update the user object in the parent component.
 */
const LoginForm = ({ setAuth, user, setUser }: LoginFormProps) => {
  const [show, setShow] = useState(false);
  const [err, setErr] = useState("");
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    setUser({
      ...user,
      [e.target.name]: e.target.value,
    });
  };

  const handleLogin = (e: React.SyntheticEvent) => {
    e.preventDefault();
    axios
      .post(`${import.meta.env.VITE_API_URL}/login`, user, {
        headers: { "Content-Type": "application/json" },
      })
      .then(res => {
        const jwtToken = res.headers.authorization;

        if (jwtToken != null) {
          sessionStorage.setItem("jwt", jwtToken);
          setAuth(true);
        }

        updateStatus(user.username, "Available");
        sessionStorage.setItem("user", JSON.stringify(user));
      })
      .catch(err => {
        setErr(err.response.data);
        setShow(true);
      });
  };

  return (
    <div>
      <Form onSubmit={handleLogin}>
        <Form.Group className='mb-3' controlId='username'>
          <Form.Control
            name='username'
            type='text'
            placeholder='Username'
            onChange={handleChange}
          />
        </Form.Group>

        <Form.Group className='mb-3' controlId='password'>
          <Form.Control
            name='password'
            type='password'
            placeholder='Password'
            className='mb-3'
            onChange={handleChange}
          />
          <Link to={"/forgotpassword"}>Forgot Password?</Link>
        </Form.Group>

        <Button variant='primary' className='me-3' type='submit'>
          Submit
        </Button>

        <Button variant='outline-primary' onClick={() => navigate("/register")}>
          Create Account
        </Button>
      </Form>
      <ErrorToast title='Login Failed' msg={err} show={show} setShow={setShow} />
    </div>
  );
};

export default LoginForm;
