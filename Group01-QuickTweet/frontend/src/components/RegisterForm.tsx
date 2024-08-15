import { Button, Form, Col, Row } from "react-bootstrap";
import { useNavigate } from "react-router";
import { useFormik } from "formik";
import * as Yup from "yup";
import { useState } from "react";
import ErrorToast from "./ErrorToast";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleLeft } from "@fortawesome/free-regular-svg-icons";
import { createUserRequest } from "../api/userApi";

/**
 * `RegisterForm` is a React component for rendering a user registration form.
 *
 * This component utilizes React Bootstrap for styling and layout, providing a user-friendly interface for registration.
 * It includes form fields for email, username, password, role, a security question, and an answer to the security question.
 * The form uses Formik for form state management and Yup for validation, ensuring that user inputs meet specific criteria
 * before submission.
 *
 * The email field specifically requires a valid Dalhousie University email address. The password field enforces a minimum
 * length and a mix of character types for security. Upon successful validation and submission, a user creation request is
 * sent to the server via the `createUserRequest` API function.
 *
 * Error handling is implemented to display feedback to the user in case of an invalid submission attempt or an error during
 * the user creation process. A loading state is also managed to provide visual feedback during the submission process.
 *
 * @component
 * @returns A React element that renders the registration form with validation and error handling.
 */
const RegisterForm = () => {
  const [err, setErr] = useState("");
  const [show, setShow] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();
  const securityQuestions = [
    "What is the name of your first pet?",
    "What is your grandmother's maiden name?",
    "What is your favorite drink?",
  ];

  const formik = useFormik({
    initialValues: {
      email: "",
      username: "",
      password: "",
      role: "",
      securityQuestion: "",
      securityQuestionAnswer: "",
    },
    validationSchema: Yup.object({
      email: Yup.string()
        .email("Invalid email address")
        .matches(/[A-Z0-9._%+-]+@dal\.ca/i, "Please choose a valid Dalhousie email")
        .required("Required"),
      username: Yup.string().required("Required"),
      password: Yup.string()
        .min(8, "Password must be at least 8 characters long")
        .matches(
          /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})/,
          "Password must have at least 1 uppercase character, 1 lowercase character, 1 number and 1 special character"
        )
        .required("Required"),
      securityQuestion: Yup.string().required("Required"),
      securityQuestionAnswer: Yup.string().required("Required"),
    }),
    onSubmit: async values => {
      if (isSubmitting) return;

      setIsSubmitting(true);
      const user = {
        email: values.email,
        username: values.username,
        password: values.password,
        role: "USER",
        securityQuestion: values.securityQuestion,
        securityQuestionAnswer: values.securityQuestionAnswer,
      };

      try {
        const res = await createUserRequest(user);
        console.log(res);
        navigate("/");
      } catch (err: any) {
        setErr(err.msg);
        setShow(true);
      } finally {
        setIsSubmitting(false);
      }
    },
  });

  return (
    <>
      <Form onSubmit={formik.handleSubmit}>
        <Row className='mb-3'>
          <Form.Group as={Col} controlId='email'>
            <Form.Control
              type='email'
              placeholder='Email'
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.email}
            />
            <Form.Text className='text-danger'>
              {formik.touched.email && formik.errors.email ? (
                <div className='text-danger'>{formik.errors.email}</div>
              ) : null}
            </Form.Text>
          </Form.Group>

          <Form.Group as={Col} controlId='username'>
            <Form.Control
              type='text'
              placeholder='Username'
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.username}
            />
            {formik.touched.username && formik.errors.username ? (
              <div className='text-danger'>{formik.errors.username}</div>
            ) : null}
          </Form.Group>
        </Row>

        <Row>
          <Form.Group as={Col} className='mb-3' controlId='password'>
            <Form.Control
              type='password'
              placeholder='Password'
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.password}
            />
            {formik.touched.password && formik.errors.password ? (
              <div className='text-danger'>{formik.errors.password}</div>
            ) : null}
          </Form.Group>
        </Row>

        <Row>
          <Form.Group className='mb-3'>
            <Form.Select
              name='securityQuestion'
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.securityQuestion}
            >
              <option disabled value=''>
                Security Question
              </option>
              {securityQuestions.map((question, i) => (
                <option key={i} value={question}>
                  {question}
                </option>
              ))}
            </Form.Select>
            {formik.touched.securityQuestion && formik.errors.securityQuestion ? (
              <div className='text-danger'>{formik.errors.securityQuestion}</div>
            ) : null}
          </Form.Group>
        </Row>

        <Row>
          <Form.Group className='mb-3'>
            <Form.Control
              name='securityQuestionAnswer'
              type='text'
              placeholder='Answer'
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              value={formik.values.securityQuestionAnswer}
            />
            {formik.touched.securityQuestionAnswer && formik.errors.securityQuestionAnswer ? (
              <div className='text-danger'>{formik.errors.securityQuestionAnswer}</div>
            ) : null}
          </Form.Group>
        </Row>

        <Button variant='outline-primary' type='submit' onClick={formik.submitForm}>
          Create Account
        </Button>

        <Button variant='primary' className='mx-3' onClick={() => navigate("/")}>
          <FontAwesomeIcon icon={faCircleLeft} />
        </Button>
      </Form>

      <ErrorToast title='Account could not be created' msg={err} show={show} setShow={setShow} />
    </>
  );
};

export default RegisterForm;
