import { faCircleLeft } from "@fortawesome/free-regular-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import ErrorToast from "./ErrorToast";
import { useFormik } from "formik";
import * as Yup from "yup";
import { updatePassword } from "../api/userApi";
import UsernameForm from "./UsernameForm";

/**
 * Renders a form for users to input their username when they have forgotten their password.
 * Upon submission, it attempts to fetch the security question associated with the username.
 * If successful, it updates the parent component's state with the username and security question.
 * In case of an error, it displays an error message and allows the user to try again.
 *
 * @component
 * @param {Object} props - The component props.
 * @param {Function} props.navigate - Function from `useNavigate` hook for navigation.
 * @param {Dispatch<SetStateAction<string>>} props.setUsername - Function to update the username in the parent component's state.
 * @param {Dispatch<SetStateAction<string>>} props.setSecurityQuestion - Function to update the security question in the parent component's state.
 * @param {Dispatch<SetStateAction<string>>} props.setErr - Function to update the error message in the parent component's state.
 * @param {Dispatch<SetStateAction<boolean>>} props.setShow - Function to control the visibility of the error message.
 * @returns {React.ReactElement} The UsernameForm component.
 */
const ForgotPasswordForm = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [securityQuestion, setSecurityQuestion] = useState("");
  const [err, setErr] = useState("");
  const [show, setShow] = useState(false);

  const formik = useFormik({
    initialValues: {
      password: "",
      answer: "",
    },
    validationSchema: Yup.object({
      password: Yup.string()
        .min(8, "Password must be at least 8 characters long")
        .matches(
          /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})/,
          "Password must have at least 1 uppercase character, 1 lowercase character, 1 number and 1 special character"
        )
        .required("Required"),
      answer: Yup.string().required(),
    }),
    onSubmit: async values => {
      try {
        await updatePassword(username, values);
        navigate("/");
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
      } catch (err: any) {
        setErr(err.msg);
        setShow(true);
      }
    },
  });

  return (
    <>
      <ErrorToast title='Invalid Username or Password' msg={err} show={show} setShow={setShow} />
      {username !== "" ? (
        <Form onSubmit={formik.handleSubmit}>
          <Form.Group className='mb-3' controlId='password'>
            <Form.Control
              type='password'
              placeholder='New Password'
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.password}
            />
            {formik.touched.password && formik.errors.password ? (
              <div className='text-danger'>{formik.errors.password}</div>
            ) : null}
          </Form.Group>

          <Form.Group className='mb-3' controlId='securityQuestion'>
            <Form.Control disabled type='text' value={securityQuestion} />
          </Form.Group>

          <Form.Group className='mb-3' controlId='answer'>
            <Form.Control
              required
              type='text'
              placeholder='Answer'
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.answer}
            />
            {formik.touched.answer && formik.errors.answer ? (
              <div className='text-danger'>{formik.errors.answer}</div>
            ) : null}
          </Form.Group>

          <Button variant='outline-primary' onClick={formik.submitForm}>
            Update Password
          </Button>

          <Button variant='primary' className='mx-3' onClick={() => navigate("/")}>
            <FontAwesomeIcon icon={faCircleLeft} />
          </Button>
        </Form>
      ) : (
        <UsernameForm
          navigate={navigate}
          setUsername={setUsername}
          setSecurityQuestion={setSecurityQuestion}
          setErr={setErr}
          setShow={setShow}
        />
      )}
    </>
  );
};

export default ForgotPasswordForm;
