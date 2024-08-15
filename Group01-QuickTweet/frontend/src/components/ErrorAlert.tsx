import { faHouse } from "@fortawesome/free-solid-svg-icons/faHouse";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Alert, Button, Container } from "react-bootstrap";
import { useNavigate } from "react-router";
import { Link } from "react-router-dom";

const ErrorAlert = () => {
  const navigate = useNavigate();
  return (
    <Container className='mt-5'>
      <Button variant='outline-secondary' onClick={() => navigate("/")} className='mb-3'>
        <FontAwesomeIcon icon={faHouse} />
      </Button>
      <Alert variant='danger' show={true} onClose={() => navigate("/")} dismissible>
        <Alert.Heading>Oh snap! The user does not exist!</Alert.Heading>
        <p>
          The user you're looking for does not exist. Please check the username and try again!
          <br />
          <Link to='/'>Home</Link>
        </p>
      </Alert>
    </Container>
  );
};

export default ErrorAlert;
