import { Col, Container, Row } from "react-bootstrap";
import Image from "react-bootstrap/Image";
import { FormProps } from "../types";

/**
 * `UserLoginPage` is a React component that renders the user login page.
 *
 * This component displays a login page layout consisting of a background image and a login form. The page is divided into
 * two main columns using Bootstrap's Grid system: one for the background image and the other for the login form. The login
 * form itself is passed as a prop to allow for flexibility and reuse of the form component in different contexts.
 *
 * @component
 * @param {FormProps} props - The properties passed to the UserLoginPage component, including the form component and its elements.
 * @returns A React element that renders the login page with a background image and a login form.
 */
const UserLoginPage = ({ Form, formComponents }: FormProps) => {
  const style = {
    maxWidth: "100vw",
    maxHeight: "100vh",
    backgroundSize: "contain",
  };

  const iconStyle = {
    maxWidth: "15vw",
    maxHeight: "auto",
    backgroundSize: "contain",
  };

  return (
    <Container>
      <Row>
        <Col>
          <Image style={style} className='mx-auto d-block' src={import.meta.env.VITE_LOGIN_BACKGROUND_PATH} />
        </Col>
        <Col className='pe-5' style={{ height: "100vh", width: "100%" }}>
          <Row style={{ height: "40vh", width: "100%" }} className='align-items-center'>
            <Col>
              <h1 className='ps-2 align-text-top display-4' style={{ display: "inline" }}>
                QuickTweet
              </h1>
              <Image style={iconStyle} src={import.meta.env.VITE_LOGO_PATH} />
            </Col>
          </Row>
          <Row style={{ height: "60vh", width: "100%" }} className='align-items-˝start'>
            <Col>
              <Form {...formComponents} />
            </Col>
          </Row>
        </Col>
      </Row>
    </Container>
  );
};

export default UserLoginPage;
