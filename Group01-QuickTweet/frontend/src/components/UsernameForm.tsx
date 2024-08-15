import { faCircleLeft } from "@fortawesome/free-regular-svg-icons/faCircleLeft";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, Col, Form, Row } from "react-bootstrap";
import { getSecurityQuestion } from "../api/userApi";
import { FormEvent } from "react";
import { UsernameFormProps } from "../types";

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
const UsernameForm = ({
  navigate,
  setUsername,
  setSecurityQuestion,
  setErr,
  setShow,
}: UsernameFormProps): React.ReactElement => {
  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const username: string = e.currentTarget.username.value;
    try {
      const res = await getSecurityQuestion(username);
      setUsername(username);
      setSecurityQuestion(res);
    } catch (err: any) {
      setErr(err.request.responseText);
      setShow(true);
      setUsername("");
    }
  };

  return (
    <Form onSubmit={handleSubmit}>
      <Row>
        <Form.Group as={Col} className='mb-3' controlId='username'>
          <Form.Control type='text' placeholder='Username' />
        </Form.Group>
      </Row>

      <Button variant='outline-primary' type='submit'>
        Update Password
      </Button>

      <Button variant='primary' className='mx-3' onClick={() => navigate("/")}>
        <FontAwesomeIcon icon={faCircleLeft} />
      </Button>
    </Form>
  );
};

export default UsernameForm;
