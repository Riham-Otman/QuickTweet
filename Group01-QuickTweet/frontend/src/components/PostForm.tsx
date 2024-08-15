import { FormEvent } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import { createPost } from "../api/postApi";
import { PostFormProps } from "../types";

/**
 * `PostForm` is a React component that renders a modal form for creating a new post.
 *
 * The component encapsulates a modal dialog that prompts the user to enter the content for a new post. Upon submission,
 * it leverages the `createPost` function to send the post data to the server. The modal's visibility is controlled by
 * the `modal` state, which can be toggled on or off. The `setLoading` function is called to indicate that the post creation
 * process has started, providing feedback to the user.
 *
 * @component
 * @returns The `PostForm` component renders a modal with a form for creating a new post.
 */
const PostForm = ({ modal, setModal, user, setLoading }: PostFormProps) => {
  const onSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    await createPost(user, e.currentTarget.content.value);
    setModal(false);
    setLoading(true);
  };

  return (
    <Modal
      show={modal}
      onHide={() => {
        setModal(false);
      }}
    >
      <Modal.Header closeButton>
        <Modal.Title>Create a new post</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form onSubmit={onSubmit}>
          <Form.Group className='mb-3' controlId='content'>
            <Form.Label>Content</Form.Label>
            <Form.Control as='textarea' style={{ height: "150px" }} autoFocus />
          </Form.Group>
          <Button variant='secondary' className='me-3' onClick={() => setModal(false)}>
            Close
          </Button>
          <Button variant='primary' type='submit'>
            Create Post
          </Button>
        </Form>
      </Modal.Body>
    </Modal>
  );
};

export default PostForm;
